# Implementation Plan - Dian Catering UI Refinement

Refine the UI consistency, spacing, and dashboard data loading for the Dian Catering application.

## User Review Required

> [!IMPORTANT]
> All menu icons will be switched to an "Outline" monochrome style and placed inside a soft gold circular badge.

> [!NOTE]
> Section spacing will be normalized to 12dp, with 16dp internal padding for cards and 16dp horizontal margins from the screen edges.

## Proposed Changes

### Assets & Styling

#### [NEW] [bg_icon_badge.xml](file:///D:/lesson/project/iseng/app/src/main/res/drawable/bg_icon_badge.xml)
- Circular shape with #33D9A441 (20% opacity Gold) solid fill.

#### [NEW] [ic_group_outline.xml](file:///D:/lesson/project/iseng/app/src/main/res/drawable/ic_group_outline.xml)
- Outline monochrome version of the group/people icon.

#### [NEW] [ic_restaurant_outline.xml](file:///D:/lesson/project/iseng/app/src/main/res/drawable/ic_restaurant_outline.xml)
- Outline monochrome version of the restaurant icon.

#### [NEW] [ic_settings_outline.xml](file:///D:/lesson/project/iseng/app/src/main/res/drawable/ic_settings_outline.xml)
- Outline monochrome version of the settings icon.

#### [NEW] [ic_logout_outline.xml](file:///D:/lesson/project/iseng/app/src/main/res/drawable/ic_logout_outline.xml)
- Outline monochrome version of the logout icon.

### Dashboard (MainActivity)

#### [MODIFY] [activity_main.xml](file:///D:/lesson/project/iseng/app/src/main/res/layout/activity_main.xml)
- Update menu grid cards:
    - Wrap icons in a 44dp circular badge (using `bg_icon_badge`).
    - Set icon tint to `#F3E6D8` (off-white).
    - Set label tint to gold (`#D9A441`).
- Normalize spacing:
    - 12dp margin between sections.
    - 16dp horizontal margin for all cards.
    - 16dp internal padding for cards.
- Update Laporan preview card layout to include row dividers.

#### [MODIFY] [MainActivity.java](file:///D:/lesson/project/iseng/app/src/main/java/com/example/iseng/MainActivity.java)
- Update `populateLaporanPreview` to render rows with a horizontal layout (Jurusan on left, count on right) and thin dividers.
- Add error logging in `onCancelled` to debug Firebase issues.

#### [MODIFY] [RiwayatRepository.java](file:///D:/lesson/project/iseng/app/src/main/java/com/example/iseng/RiwayatRepository.java)
- Add error logging in `onCancelled`.
- Optimization: Consider checking if listening to root is causing permission denials.

### Consistency Across Activities

#### [MODIFY] Other Activities
- Apply 12dp section spacing and 16dp card margins/padding where applicable.
- Affected: `activity_daftar_siswa.xml`, `activity_tambah_siswa.xml`, `activity_detail_siswa.xml`, `activity_laporan.xml`, `activity_pengaturan.xml`.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to verify build integrity.

### Manual Verification
- Verify icon consistency and badge appearance on the dashboard.
- Check spacing and alignment against the 12dp/16dp standards.
- Confirm the Laporan preview card loads real data and handles the "Memuat data..." state correctly.
- Check logs for any Firebase errors.
