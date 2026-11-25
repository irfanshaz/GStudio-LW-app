#!/usr/bin/env bash
set -euo pipefail
baseline=0bb573860dcd3199cbc48deecaebec7f3c646f20
git cat-file -e "$baseline^{commit}" 2>/dev/null || git fetch --depth=1 origin "$baseline"
# These files own persisted IDs, schema, lessons, supplied media, licenses and saved progress.
git diff --exit-code "$baseline" HEAD -- \
  app/src/main/res \
  app/src/main/assets \
  app/src/main/java/com/example/model \
  app/src/main/java/com/example/data \
  docs/media
printf 'Preservation check passed: catalog, IDs, schema, assets and media manifests match baseline.\n'
