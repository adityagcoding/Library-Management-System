🚀 Click Here to View Live Project# Library Management System
https://adityagcoding.github.io/Library-Management-System/

A library management project containing a browser-based interface, Java source code, and a MySQL database schema.

## Project structure

```text
Library-Management-System/
├── index.html          # Website entry point for GitHub Pages
├── src/                # Java application source code
│   ├── Main.java
│   ├── dao/
│   ├── database/
│   ├── model/
│   └── ui/
└── sql/
    └── library_db.sql # MySQL database schema/script
```

## View the website with GitHub Pages

1. Create a **public** GitHub repository named `Library-Management-System`.
2. Upload the contents of this folder (not this ZIP file). Keep `index.html` in the repository root.
3. In the repository, open **Settings → Pages**.
4. Under **Build and deployment**, choose **Deploy from a branch**, select `main` and `/(root)`, then save.
5. After deployment, the website will be available at `https://YOUR-USERNAME.github.io/Library-Management-System/`.

## Important deployment note

GitHub Pages serves static frontend files such as HTML, CSS, and JavaScript. It does not execute Java code or host a MySQL database. The `src/` Java application and `sql/` database script are included as project source files; database-backed functionality requires a separately hosted Java backend and MySQL database.

## Database

The SQL script is in `sql/library_db.sql`. Review it and configure your own local or hosted MySQL connection before running the Java application. Do not commit real passwords, API keys, or other secrets.
