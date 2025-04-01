# Localization

## Supported Languages
1. English(Default)
2. Chinese
3. Japanese

## to change the language
* The global language converter, with one button for each language, can switch the interface language in real time. The interface will be rendered in real time according to the language selected by the user and display the correct text.
* On the interface, we use a listener to listen to the language selector. Once the language is changed, the interface will automatically re-render with the text in the corresponding language.

## Database Localization
* We use a separated Translation Table to store the translation of the text in different languages. The Translation Table is a key-value pair table, where the key is the original text in English and the value is the translated text in the target language.

## Google Translation API
* We use the Google Translation API to translate the text in the Translation Table from English to the target language. The Google Translation API is a powerful tool that can translate the text in real time and provide accurate translation results.
