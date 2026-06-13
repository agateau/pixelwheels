# Signing & notarizing the macOS build

The macOS `.app` is built by `tools/packaging/create-macos-archive` (via
`make macos-archive`) and packaged by jpackage. Signing and notarization are
**optional** and controlled entirely by environment variables / CI secrets.

## Without signing (default)

If no signing identity is provided, the build produces a working **unsigned**
`.app`. macOS Gatekeeper will quarantine it, so the first launch shows a
"Pixel Wheels is damaged and can't be opened" dialog. Users can clear the
quarantine attribute:

```sh
xattr -cr "Pixel Wheels.app"
```

This is the same situation as the previous packr-based builds.

## With signing + notarization (recommended for releases)

To ship a build that opens with no warnings, the maintainer needs an Apple
Developer account ($99/yr) and must provide credentials. None of this is in the
repo — it requires secrets only the maintainer holds.

### One-time setup

1. **Developer ID Application certificate** — create one in the Apple Developer
   portal, export it as a password-protected `.p12`, and store (base64-encoded)
   as the `APPLE_CERT_P12_BASE64` secret plus `APPLE_CERT_PASSWORD`.
2. **notarytool credentials** — an App Store Connect API key (or an
   app-specific password). Store them so a keychain profile can be created on
   the runner.

### CI secrets consumed by the workflow

| Secret | Purpose |
|---|---|
| `APPLE_SIGNING_IDENTITY` | The identity name, e.g. `Your Name (TEAMID)`. Enables `jpackage --mac-sign`. |
| `AC_NOTARY_KEYCHAIN_PROFILE` | Name of the notarytool keychain profile to submit with. |

`create-macos-archive` already reads these. When `APPLE_SIGNING_IDENTITY` is
set it signs; when a signed app plus `AC_NOTARY_KEYCHAIN_PROFILE` are both
present it notarizes and staples.

### The missing CI step (maintainer must add)

Before `make macos-archive`, the `build-macos` job needs a step that imports
the certificate into a temporary keychain and registers the notarytool
profile. This is intentionally **not** committed because it does nothing
without the secrets above. Example:

```yaml
      - name: Import signing certificate
        if: ${{ secrets.APPLE_CERT_P12_BASE64 != '' }}
        env:
          APPLE_CERT_P12_BASE64: ${{ secrets.APPLE_CERT_P12_BASE64 }}
          APPLE_CERT_PASSWORD: ${{ secrets.APPLE_CERT_PASSWORD }}
          AC_API_KEY_ID: ${{ secrets.AC_API_KEY_ID }}
          AC_API_ISSUER_ID: ${{ secrets.AC_API_ISSUER_ID }}
          AC_API_KEY_BASE64: ${{ secrets.AC_API_KEY_BASE64 }}
        run: |
          KEYCHAIN=build.keychain
          security create-keychain -p "" "$KEYCHAIN"
          security set-keychain-settings "$KEYCHAIN"
          security unlock-keychain -p "" "$KEYCHAIN"
          security list-keychains -d user -s "$KEYCHAIN" login.keychain
          echo "$APPLE_CERT_P12_BASE64" | base64 --decode > cert.p12
          security import cert.p12 -k "$KEYCHAIN" -P "$APPLE_CERT_PASSWORD" \
            -T /usr/bin/codesign
          security set-key-partition-list -S apple-tool:,apple: -s -k "" "$KEYCHAIN"
          # Register the notarytool profile referenced by AC_NOTARY_KEYCHAIN_PROFILE
          echo "$AC_API_KEY_BASE64" | base64 --decode > api_key.p8
          xcrun notarytool store-credentials "$AC_NOTARY_KEYCHAIN_PROFILE" \
            --key api_key.p8 --key-id "$AC_API_KEY_ID" --issuer "$AC_API_ISSUER_ID"
```

Once that step and the secrets are in place, releases are signed and notarized
automatically; nothing else in the pipeline changes.
