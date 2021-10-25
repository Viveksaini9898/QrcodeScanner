/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qr.scanner.encode;

import static com.qr.scanner.encode.ContactEncoder.trim;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import com.core.BarcodeFormat;
import com.core.EncodeHintType;
import com.core.MultiFormatWriter;
import com.core.Result;
import com.core.WriterException;

import com.core.client.result.AddressBookParsedResult;
import com.core.client.result.ParsedResult;
import com.core.client.result.ResultParser;
import com.core.common.BitMatrix;
import com.qr.scanner.Contents;
import com.qr.scanner.Intents;
import com.qr.scanner.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This class does the work of decoding the user's request and extracting all the data
 * to be encoded in a barcode.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class QRCodeEncoder {

  private static  int WHITE = 0xFFFFFFFF;
  private static  int BLACK = 0xFF000000;
  private boolean encoded = false;

  private  Context activity;
  private String contents;
  private String displayContents;
  private String title;
  private BarcodeFormat format;
  private  int dimension;
  private boolean useVCard;



  String getContents() {
    return contents;
  }

  String getDisplayContents() {
    return displayContents;
  }

  String getTitle() {
    return title;
  }

  boolean isUseVCard() {
    return useVCard;
  }

  // It would be nice if the string encoding lived in the core ZXing library,
  // but we use platform specific code like PhoneNumberUtils, so it can't.
  private void encodeContentsFromZXingIntent(Intent intent) {
     // Default to QR_CODE if no format given.
    String formatString = intent.getStringExtra(Intents.Encode.FORMAT);
    format = null;
    if (formatString != null) {
      try {
        format = BarcodeFormat.valueOf(formatString);
      } catch (IllegalArgumentException iae) {
        // Ignore it then
      }
    }
    if (format == null || format == BarcodeFormat.QR_CODE) {
      String type = intent.getStringExtra(Intents.Encode.TYPE);
      if (type != null && !type.isEmpty()) {
        this.format = BarcodeFormat.QR_CODE;
        encodeQRCodeContents(intent, type);
      }
    } else {
      String data = intent.getStringExtra(Intents.Encode.DATA);
      if (data != null && !data.isEmpty()) {
        contents = data;
        displayContents = data;
        title = activity.getString(R.string.contents_text);
      }
    }
  }

  // Handles send intents from multitude of Android applications
  private void encodeContentsFromShareIntent(Intent intent) throws WriterException {
    // Check if this is a plain text encoding, or contact
    if (intent.hasExtra(Intent.EXTRA_STREAM)) {
      encodeFromStreamExtra(intent);
    } else {
      encodeFromTextExtras(intent);
    }
  }

  private void encodeFromTextExtras(Intent intent) throws WriterException {
    // Notice: Google Maps shares both URL and details in one text, bummer!
    String theContents = trim(intent.getStringExtra(Intent.EXTRA_TEXT));
    if (theContents == null) {
      theContents = trim(intent.getStringExtra("android.intent.extra.HTML_TEXT"));
      // Intent.EXTRA_HTML_TEXT
      if (theContents == null) {
        theContents = trim(intent.getStringExtra(Intent.EXTRA_SUBJECT));
        if (theContents == null) {
          String[] emails = intent.getStringArrayExtra(Intent.EXTRA_EMAIL);
          if (emails != null) {
            theContents = trim(emails[0]);
          } else {
            theContents = "?";
          }
        }
      }
    }

    // Trim text to avoid URL breaking.
    if (theContents == null || theContents.isEmpty()) {
      throw new WriterException("Empty EXTRA_TEXT");
    }
    contents = theContents;
    // We only do QR code.
    format = BarcodeFormat.QR_CODE;
    if (intent.hasExtra(Intent.EXTRA_SUBJECT)) {
      displayContents = intent.getStringExtra(Intent.EXTRA_SUBJECT);
    } else if (intent.hasExtra(Intent.EXTRA_TITLE)) {
      displayContents = intent.getStringExtra(Intent.EXTRA_TITLE);
    } else {
      displayContents = contents;
    }
    title = activity.getString(R.string.contents_text);
  }

  // Handles send intents from the Contacts app, retrieving a contact as a VCARD.
  private void encodeFromStreamExtra(Intent intent) throws WriterException {
    format = BarcodeFormat.QR_CODE;
    Bundle bundle = intent.getExtras();
    if (bundle == null) {
      throw new WriterException("No extras");
    }
    Uri uri = bundle.getParcelable(Intent.EXTRA_STREAM);
    if (uri == null) {
      throw new WriterException("No EXTRA_STREAM");
    }
    byte[] vcard;
    String vcardString;
    try (InputStream stream = activity.getContentResolver().openInputStream(uri)) {
      if (stream == null) {
        throw new WriterException("Can't open stream for " + uri);
      }
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[2048];
      int bytesRead;
      while ((bytesRead = stream.read(buffer)) > 0) {
        baos.write(buffer, 0, bytesRead);
      }
      vcard = baos.toByteArray();
      vcardString = new String(vcard, 0, vcard.length, StandardCharsets.UTF_8);
    } catch (IOException ioe) {
      throw new WriterException(ioe);
    }
    Result result = new Result(vcardString, vcard, null, BarcodeFormat.QR_CODE);
    ParsedResult parsedResult = ResultParser.parseResult(result);
    if (!(parsedResult instanceof AddressBookParsedResult)) {
      throw new WriterException("Result was not an address");
    }
    encodeQRCodeContents((AddressBookParsedResult) parsedResult);
    if (contents == null || contents.isEmpty()) {
      throw new WriterException("No content to encode");
    }
  }

  private void encodeQRCodeContents(Intent intent, String type) {
    switch (type) {
      case Contents.Type.TEXT:
        String textData = intent.getStringExtra(Intents.Encode.DATA);
        if (textData != null && !textData.isEmpty()) {
          contents = textData;
          displayContents = textData;
          title = activity.getString(R.string.contents_text);
        }
        break;

      case Contents.Type.EMAIL:
        String emailData = trim(intent.getStringExtra(Intents.Encode.DATA));
        if (emailData != null) {
          contents = "mailto:" + emailData;
          displayContents = emailData;
          title = activity.getString(R.string.contents_email);
        }
        break;

      case Contents.Type.PHONE:
        String phoneData = trim(intent.getStringExtra(Intents.Encode.DATA));
        if (phoneData != null) {
          contents = "tel:" + phoneData;
          displayContents = ContactEncoder.formatPhone(phoneData);
          title = activity.getString(R.string.contents_phone);
        }
        break;

      case Contents.Type.SMS:
        String smsData = trim(intent.getStringExtra(Intents.Encode.DATA));
        if (smsData != null) {
          contents = "sms:" + smsData;
          displayContents = ContactEncoder.formatPhone(smsData);
          title = activity.getString(R.string.contents_sms);
        }
        break;

      case Contents.Type.CONTACT:
        Bundle contactBundle = intent.getBundleExtra(Intents.Encode.DATA);
        if (contactBundle != null) {

          String name = contactBundle.getString(ContactsContract.Intents.Insert.NAME);
          String organization = contactBundle.getString(ContactsContract.Intents.Insert.COMPANY);
          String address = contactBundle.getString(ContactsContract.Intents.Insert.POSTAL);
          List<String> phones = getAllBundleValues(contactBundle, Contents.PHONE_KEYS);
          List<String> phoneTypes = getAllBundleValues(contactBundle, Contents.PHONE_TYPE_KEYS);
          List<String> emails = getAllBundleValues(contactBundle, Contents.EMAIL_KEYS);
          String url = contactBundle.getString(Contents.URL_KEY);
          List<String> urls = url == null ? null : Collections.singletonList(url);
          String note = contactBundle.getString(Contents.NOTE_KEY);

          ContactEncoder encoder = useVCard ? new VCardContactEncoder() : new MECARDContactEncoder();
          String[] encoded = encoder.encode(Collections.singletonList(name),
                                            organization,
                                            Collections.singletonList(address),
                                            phones,
                                            phoneTypes,
                                            emails,
                                            urls,
                                            note);
          // Make sure we've encoded at least one field.
          if (!encoded[1].isEmpty()) {
            contents = encoded[0];
            displayContents = encoded[1];
            title = activity.getString(R.string.contents_contact);
          }

        }
        break;

      case Contents.Type.LOCATION:
        Bundle locationBundle = intent.getBundleExtra(Intents.Encode.DATA);
        if (locationBundle != null) {
          // These must use Bundle.getFloat(), not getDouble(), it's part of the API.
          float latitude = locationBundle.getFloat("LAT", Float.MAX_VALUE);
          float longitude = locationBundle.getFloat("LONG", Float.MAX_VALUE);
          if (latitude != Float.MAX_VALUE && longitude != Float.MAX_VALUE) {
            contents = "geo:" + latitude + ',' + longitude;
            displayContents = latitude + "," + longitude;
            title = activity.getString(R.string.contents_location);
          }
        }
        break;
    }
  }

  private static List<String> getAllBundleValues(Bundle bundle, String[] keys) {
    List<String> values = new ArrayList<>(keys.length);
    for (String key : keys) {
      Object value = bundle.get(key);
      values.add(value == null ? null : value.toString());
    }
    return values;
  }

  private void encodeQRCodeContents(AddressBookParsedResult contact) {
    ContactEncoder encoder = useVCard ? new VCardContactEncoder() : new MECARDContactEncoder();
    String[] encoded = encoder.encode(toList(contact.getNames()),
                                      contact.getOrg(),
                                      toList(contact.getAddresses()),
                                      toList(contact.getPhoneNumbers()),
                                      null,
                                      toList(contact.getEmails()),
                                      toList(contact.getURLs()),
                                      null);
    // Make sure we've encoded at least one field.
    if (!encoded[1].isEmpty()) {
      contents = encoded[0];
      displayContents = encoded[1];
      title = activity.getString(R.string.contents_contact);
    }
  }

  private static List<String> toList(String[] values) {
    return values == null ? null : Arrays.asList(values);
  }

  public QRCodeEncoder(String data, Bundle bundle, String type, String format, int dimension) {
    this.dimension = dimension;
    encoded = encodeContents(data, bundle, type, format);
  }


  private boolean encodeContents(String data, Bundle bundle, String type, String formatString) {
    // Default to QR_CODE if no format given.
    format = null;
    if (formatString != null) {
      try {
        format = BarcodeFormat.valueOf(formatString);
      } catch (IllegalArgumentException iae) {
        // Ignore it then
      }
    }
    if (format == null || format == BarcodeFormat.QR_CODE) {
      this.format = BarcodeFormat.QR_CODE;
      encodeQRCodeContents(data, bundle, type);
    } else if (data != null && data.length() > 0) {
      contents = data;
      displayContents = data;
      title = "Text";
    }
    return contents != null && contents.length() > 0;
  }

  private void encodeQRCodeContents(String data, Bundle bundle, String type) {
    if (type.equals(Contents.Type.TEXT)) {
      if (data != null && data.length() > 0) {
        contents = data;
        displayContents = data;
        title = "Text";
      }
    } else if (type.equals(Contents.Type.EMAIL)) {
      data = trim(data);
      if (data != null) {
        contents = "mailto:" + data;
        displayContents = data;
        title = "E-Mail";
      }
    } else if (type.equals(Contents.Type.PHONE)) {
      data = trim(data);
      if (data != null) {
        contents = "tel:" + data;
        displayContents = PhoneNumberUtils.formatNumber(data);
        title = "Phone";
      }
    } else if (type.equals(Contents.Type.SMS)) {
      data = trim(data);
      if (data != null) {
        contents = "sms:" + data;
        displayContents = PhoneNumberUtils.formatNumber(data);
        title = "SMS";
      }
    } else if (type.equals(Contents.Type.CONTACT)) {
      if (bundle != null) {
        StringBuilder newContents = new StringBuilder(100);
        StringBuilder newDisplayContents = new StringBuilder(100);

        newContents.append("MECARD:");

        String name = trim(bundle.getString(ContactsContract.Intents.Insert.NAME));
        if (name != null) {
          newContents.append("N:").append(escapeMECARD(name)).append(';');
          newDisplayContents.append(name);
        }

        String address = trim(bundle.getString(ContactsContract.Intents.Insert.POSTAL));
        if (address != null) {
          newContents.append("ADR:").append(escapeMECARD(address)).append(';');
          newDisplayContents.append('\n').append(address);
        }

        Collection<String> uniquePhones = new HashSet<String>(Contents.PHONE_KEYS.length);
        for (int x = 0; x < Contents.PHONE_KEYS.length; x++) {
          String phone = trim(bundle.getString(Contents.PHONE_KEYS[x]));
          if (phone != null) {
            uniquePhones.add(phone);
          }
        }
        for (String phone : uniquePhones) {
          newContents.append("TEL:").append(escapeMECARD(phone)).append(';');
          newDisplayContents.append('\n').append(PhoneNumberUtils.formatNumber(phone));
        }

        Collection<String> uniqueEmails = new HashSet<String>(Contents.EMAIL_KEYS.length);
        for (int x = 0; x < Contents.EMAIL_KEYS.length; x++) {
          String email = trim(bundle.getString(Contents.EMAIL_KEYS[x]));
          if (email != null) {
            uniqueEmails.add(email);
          }
        }
        for (String email : uniqueEmails) {
          newContents.append("EMAIL:").append(escapeMECARD(email)).append(';');
          newDisplayContents.append('\n').append(email);
        }

        String url = trim(bundle.getString(Contents.URL_KEY));
        if (url != null) {
          // escapeMECARD(url) -> wrong escape e.g. http\://zxing.google.com
          newContents.append("URL:").append(url).append(';');
          newDisplayContents.append('\n').append(url);
        }

        String note = trim(bundle.getString(Contents.NOTE_KEY));
        if (note != null) {
          newContents.append("NOTE:").append(escapeMECARD(note)).append(';');
          newDisplayContents.append('\n').append(note);
        }

        // Make sure we've encoded at least one field.
        if (newDisplayContents.length() > 0) {
          newContents.append(';');
          contents = newContents.toString();
          displayContents = newDisplayContents.toString();
          title = "Contact";
        } else {
          contents = null;
          displayContents = null;
        }

      }
    } else if (type.equals(Contents.Type.LOCATION)) {
      if (bundle != null) {
        // These must use Bundle.getFloat(), not getDouble(), it's part of the API.
        float latitude = bundle.getFloat("LAT", Float.MAX_VALUE);
        float longitude = bundle.getFloat("LONG", Float.MAX_VALUE);
        if (latitude != Float.MAX_VALUE && longitude != Float.MAX_VALUE) {
          contents = "geo:" + latitude + ',' + longitude;
          displayContents = latitude + "," + longitude;
          title = "Location";
        }
      }
    }
  }

  public Bitmap encodeAsBitmap() throws WriterException {
    String contentsToEncode = contents;
    if (contentsToEncode == null) {
      return null;
    }
    Map<EncodeHintType,Object> hints = null;
    String encoding = guessAppropriateEncoding(contentsToEncode);
    if (encoding != null) {
      hints = new EnumMap<>(EncodeHintType.class);
      hints.put(EncodeHintType.CHARACTER_SET, encoding);
    }
    BitMatrix result;
    try {
      result = new MultiFormatWriter().encode(contentsToEncode, format, dimension, dimension, hints);
    } catch (IllegalArgumentException iae) {
      // Unsupported format
      return null;
    }
    int width = result.getWidth();
    int height = result.getHeight();
    int[] pixels = new int[width * height];
    for (int y = 0; y < height; y++) {
      int offset = y * width;
      for (int x = 0; x < width; x++) {
        pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
      }
    }

    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    return bitmap;
  }

  private static String guessAppropriateEncoding(CharSequence contents) {
    // Very crude at the moment
    for (int i = 0; i < contents.length(); i++) {
      if (contents.charAt(i) > 0xFF) {
        return "UTF-8";
      }
    }
    return null;
  }

  private static String escapeMECARD(String input) {
    if (input == null || (input.indexOf(':') < 0 && input.indexOf(';') < 0)) { return input; }
    int length = input.length();
    StringBuilder result = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      char c = input.charAt(i);
      if (c == ':' || c == ';') {
        result.append('\\');
      }
      result.append(c);
    }
    return result.toString();
  }
}
