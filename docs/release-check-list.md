# Create archives

- [ ] Check source tree is clean

    git checkout dev
    git pull
    git merge origin/master
    git status

- [ ] Bump version number

    vi version.properties

- [ ] Update changelog

    vi CHANGELOG.md
    vi fastlane/metadata/android/en-US/changelogs/$version.txt

- [ ] Build archives

    Check signing key is in android/signing.gradle

    make clean-dist

- [ ] Test on computer

    make desktop-run-from-dist

- [ ] Test on device

    make android-run-from-dist

- [ ] Update screenshots in fastlane/metadata/android/en-US/images/

- [ ] git commit

- [ ] Push dev branch

    git push

- [ ] Check CI is happy

- [ ] Merge in master

    git checkout master
    git pull
    git merge --ff-only dev

- [ ] Upload gplay apk on Google Play

    Check api file is in fastlane/google-play-api.json

    make fastlane-beta

- [ ] Check Google Play is happy

- [ ] Tag and push

    make tagpush

# Upload archives

- [ ] Upload archives to itch.io

# Game page

- [ ] Update game page
    - [ ] Screenshots
    - [ ] Archive links
- [ ] Write blog post
- [ ] Publish

# Itch.io

- [ ] Update game page
    - [ ] Screenshots
- [ ] Write blog post
- [ ] Publish

# F-Droid

- [ ] Get the F-Droid version updated

# Spread

- [ ] Select relevant screenshot
- [ ] Post on:
    - [ ] Patreon
    - [ ] Ko-fi
    - [ ] Mastodon
    - [ ] Twitter
    - [ ] Relevant reddits
