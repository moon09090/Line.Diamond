# Line — a luxurious Python IDE for Android

Write, check and run real Python 3.12 on your phone. Offline. Fast. Beautiful.

## Features
- Real CPython 3.12 (Chaquopy) with numpy, pandas, matplotlib, requests, sympy, rich
- Syntax highlighting, line numbers, live syntax-error marker, smart auto-indent
- Mobile quick-key bar (TAB, `:`, brackets, quotes)
- `input()` support via the Input tab
- Hard timeout: runaway loops are stopped (configurable, default 30 s)
- Workspace manager: create (with templates), open, delete, auto-save
- AI assistant (OpenRouter): Explain / Fix / Improve — API key stored AES-256 encrypted
- Obsidian & gold dark theme

## Build
1. Install Android Studio (Koala or newer) and JDK 17.
2. `git clone` this repo → *File ▸ Open* the `Line` folder (Android Studio creates the Gradle wrapper).
3. Click **Run ▶** on a device/emulator (arm64 or x86_64).
4. Or push to GitHub: the **Build Line** workflow produces a debug APK under *Actions ▸ Artifacts*.

## Publish to GitHub
```bash
cd Line
git init && git add . && git commit -m "Initial commit: Line IDE"
git branch -M main
git remote add origin https://github.com/<you>/Line.git
git push -u origin main
```

## New in 1.1
- Inline matplotlib charts under the Output tab
- Autocomplete chips, snippets (def/for/class/try/main)
- Undo / redo, find & replace, share code, adjustable font size

## New in 1.2
- Multi-tab editing, file import, run-selection (select code, tap Run)
- 3 themes (Obsidian Gold, Dracula, Ivory Light), tidy-code formatter
- Run history, AI "Ask" with your own question, 11 snippets

## New in 1.3
- Toggle comment, copy output, more starter templates (OOP, algorithms, plots)

## Roadmap
Multi-tab editing · pip installs · themes · code formatter · debugger

## License
MIT
