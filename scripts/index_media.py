"""Index checked-in offline media packs, resolving references against actual ZIP members."""
from pathlib import Path
from zipfile import ZipFile
import hashlib
import json

root = Path(__file__).resolve().parents[1]
packs = sorted((root / 'public/assets/aistudio').glob('LWW_MediaPack_Batch*.zip'))
packs += sorted((root / 'public/assets/aistudio').glob('LWW_AnimalMedia_V4_Unified*.zip'))
members, rows = {}, {}
fields = {
    'animals': ('common_name', 'image_filename', 'audio_filename', 'video_filename'),
    'objects': ('name', 'image_filename', 'audio_filename', 'video_filename'),
    'lessons': ('title', 'thumbnail_filename', 'narration_audio_filename', 'video_filename'),
}
for pack in packs:
    with ZipFile(pack) as z:
        for entry in z.namelist():
            path = entry.removeprefix('LittleWonderWorld_MediaPack/')
            if Path(path).suffix.lower() in {'.webp', '.png', '.jpg', '.mp3', '.mp4'}:
                data = z.read(entry)
                if data:
                    members[path] = dict(archive=pack.name, entry=entry, sha256=hashlib.sha256(data).hexdigest())
            if path.startswith('metadata/') and Path(path).stem in fields:
                kind = Path(path).stem
                for row in json.loads(z.read(entry))['items']:
                    key = (kind, row['id'])
                    rows[key] = {**rows.get(key, {}), **row}

items, files = [], {}
for (kind, key), row in rows.items():
    title, image, audio, video = fields[kind]
    item = dict(id=key, kind=kind, title=row[title], facts=row.get('facts') or [row.get('usage_fact') or row.get('topic') or row[title]])
    for slot, field in [('image', image), ('audio', audio), ('video', video)]:
        path = row.get(field)
        if isinstance(path, str) and path in members:
            item[slot] = 'real-world/' + path
            files[path] = members[path]
    if any(slot in item for slot in ('image', 'audio', 'video')):
        items.append(item)

output = root / 'app/src/main/assets/real-world-index.json'
output.write_text(json.dumps(dict(items=items, files=files), indent=2, ensure_ascii=False) + '\n')
print(f'{len(items)} cards, {len(files)} files; ' + ', '.join(f'{k}: {sum(k in i for i in items)}' for k in ('image', 'audio', 'video')))
