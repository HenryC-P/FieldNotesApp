# Field Notes

A desktop application for creating, viewing, and managing field notes for ethnography coursework.

## Download

Download the latest release for your platform from the [Releases page](https://github.com/HenryC-P/FieldNotesApp/releases).

### Windows
1. Download `FieldNotes-Windows.zip`
2. Extract the zip file
3. Run `FieldNotes/FieldNotes.exe`

### macOS
1. Download `FieldNotes-macOS.zip`
2. Extract the zip file
3. Double-click `FieldNotes.app`
4. If blocked by Gatekeeper: Right-click → Open → Open

No Java installation required—the app includes its own runtime.

## Features

- Create and edit field notes with structured fields
- Record date, time, location, and setting details
- Document participants, activities, and sensory observations
- Add reflections, cultural context, questions, and themes
- Automatic local storage of all entries

## Building from Source

Requires JDK 17+.

```bash
mvn clean package
```

Run the application:
```bash
java -jar target/field-notes-app-1.0-SNAPSHOT.jar
```

## License

MIT
