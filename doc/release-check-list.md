# Create archives

- Check source tree is clean

    git checkout master
    git pull
    git status

- Bump version number
    - Makefile
    - android/AndroidManifest.xml

- Update changelog
    vi CHANGELOG.md
    git commit

- Build archives

    Check signing key is in android/signing.gradle

    make clean assets mapscreenshotgenerator packer dist apk

- Test on computer
- Test on device

- Tag and push

    make tagpush

# Upload archives

- Upload archives

# Prepare spread

- Take screenshots

# Game page

- Update game page
    - Screenshots
    - Archive links
- Write blog post
- Publish

# Google Play

- Upload apk
- Update Google Play page
- Publish

# Spread

- Post on:
    - Mastodon
    - Twitter
    - FB
