# ResilientBite for Android

Install ResilientBite.apk on an Android 8 or newer phone. This demonstration APK opens the Python app through Android WebView and supports choosing CSV and Excel files from the phone.

Start the backend from the project folder:

    python -m streamlit run forecast_app/app.py --server.address 0.0.0.0 --server.port 8502

Keep the computer and phone on the same Wi-Fi. In the APK, enter the computer's Wi-Fi address with port 8502, such as http://192.168.1.10:8502. You can find that address in the computer's Wi-Fi settings. The computer must remain running for forecasts to work. The backend uses the computer's date.

The APK was compiled and its signature verified. Physical-device testing is still pending. It uses a demonstration signing key.

To rebuild on this Mac:

    bash forecast_app/android/build.sh
