# CommunicationManager

Throughout we will refer to the restaurant app as the RApp and the client/customer app as the CApp.

## The Either<Left, Right> data structure
To better handle the various return values, which may contain error messages or successful messages, we introduce the `Either<Left, Right>` data structure.

By convention results that are errors will be stored in the Left value, while results that are successful/intended is stored in the Right value.

For example:

```java
// Construct a result using `Either<Left, Right>`.
Either<String, String> result;
if (myOperationWentGood()) {
    result = Either.right("Everything was fine!");
} else if(whatWentWrong()) {
    result = Either.left("Something went wrong");
} else {
    result = Either.left("Something went horrible wrong!");
}

// Check what we got.
if (result.isRight()) {
    doSomethingWithTheValue(result.right());
} else {
    doSomethingWithTheError(result.left());
}
```

This might seem weird at first, but it allows a lot more flexibility than simply relying on a return value or `null` as the indicator for an error (sometimes `null` can even be a valid value).


## NFC Tag
The NFC manager works by writing NDEF messages to NFC tags and then relying on the android Tag Dispatch System to read them. The reason for going with the NDEF format is to both minimize development complexity and also to allow for a maximum Android support.

When writing data to the NFC tag, it specifies the MIME type to be `application/smartorder`, and it then relies on this MIME type to be present so the Tag Dispatch System knows which app to launch. This is defined in the `intent-filter` in the `AndroidManifest.xml`.

For more information about how NFC works in Android, refer to [https://developer.android.com/guide/topics/connectivity/nfc/nfc.html](https://developer.android.com/guide/topics/connectivity/nfc/nfc.html).

NOTE: We might want to handle [foreground dispatches](https://developer.android.com/guide/topics/connectivity/nfc/advanced-nfc.html#foreground-dispatch), but let's first test if that is needed.

### Setup

To setup the `NfcManager` via `CommunicationManager`, you need to do the following steps:

1. Add permissions for reading NFC tags
2. Add an intent-filter specifying the MIME type you want to listen for
3. Instantiate the CommunicationManager (which creates the NfcAdapter internally)
4. Handle intents received from the Tag Dispatch System (in `onNewIntent`)
5. Hook up the writing of NFC data to some event
6. Handle intent dispatches in the foreground when the app is open

#### 1. Add permissions

File: `AndroidManifest.xml`

Add permissions to read NFC tags by adding the following snippet,

```xml
<uses-permission android:name="android.permission.NFC" />
 <uses-sdk android:minSdkVersion="10"/>
<uses-feature android:name="android.hardware.nfc" android:required="true" />
```

#### 2. Add intent-filter

File: `AndroidManifest.xml`

Make the app handle NFC tag readings (placed inside the main activity),

```xml
<intent-filter>
    <action android:name="android.nfc.action.NDEF_DISCOVERED"/>
    <category android:name="android.intent.category.DEFAULT"/>
    <data android:mimeType="application/smartorder" />
</intent-filter>
```

#### 3. Instantiate the CommunicationManager

File: `MainActivity` (or whichever file is your root activity)

Instantiate the NFC module with,

```java
private CommunicationManager mCommunicationManager;

@Override
protected void onCreate(Bundle savedInstanceState) {
    mCommunicationManager = new CommunicationManager(this);
}
```


#### 4. Handle intents received from the Tag Dispatch System

File: `MainActivity` (or whichever file is your root activity)

Handle new intents (i.e. new NFC reads) with `onNewIntent`,

```java
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Either<String, String> nfcMessage = mCommunicationManager.readNfcTag(intent);
    if (nfcMessage.isRight()) {
        Log.i("NFC Message", nfcMessage.right());
    }  else {
        Log.i("Error in NFC Read", nfcMessage.left());
    }
}
```

This is used both to read the NFC tag message and also to discover the NFC tag for writing on it.

#### 5. Write NFC data

File: Whichever you want

Hook the NFC tag reader up to an action,

```java
protected void onWriteButtonClick() {
    Either<String, String> writeResult = mCommunicationManager.writeNfcTag(tableNumber);
    if (writeResult.isRight()) {
        Log.i("NFC Write Result", writeResult.right());
    } else {
        Log.i("Error in NFC Write Result", writeResult.left());
    }
}
```

Note, that the `writeNfcTag` assumes you are also reading for NFC tags, since this is needed to actually detect the NFC tag.


#### 6. Handle intents in the foreground

File: `MainActivity` (or whichever file is your root activity)

```java
@Override
public void onPause() {
    super.onPause();
    mCommunicationManager.disableForegroundDispatch();
}

@Override
public void onResume() {
    super.onResume();
    mCommunicationManager.enableForegroundDispatch();
}
```


## BLE
Some terminology: There are two things to decide on at first, which app is central or peripheral and which app is the GATT server or client.

- _Central/Peripheral_: Since the RApp is stationary it will be the peripheral that advertises its services, and the CApp will be the central that scans for services.

- _GATT Server/Client_: We want the RApp to be on the receiving end, and this means that we set it up as the GATT server. The CApp will be transmitting data to the server as the GATT client.

For more information about how BLE works in Android, refer to [https://developer.android.com/guide/topics/connectivity/bluetooth-le.html](https://developer.android.com/guide/topics/connectivity/bluetooth-le.html).

### Setup

1. Add permissions for using BLE
2. ...

#### 1. Add permissions

File: `AndroidManifest.xml`

Add permissions for using BLE,

```xml
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
```

#### 2.
More to come...
