# Implementation Plan - UI Modernization and Functional Cleanup

This plan addresses the removal of dynamic settings, menu restructuring for better UX, and a complete visual standardization across the dashboard.

## User Review Required

> [!IMPORTANT]
> - **Settings Removal**: The catering price will be locked at **Rp 20.000**. `PengaturanActivity` will be deleted.
> - **Menu Transformation**: The dashboard grid will change from a mix of horizontal/vertical cards to 3 consistent vertical cards.
> - **Color Standardization**: All icons will use a muted gold (`#C9A227`) and labels will use the primary gold (`#FFD484`).

## Proposed Changes

### 1. Functional Simplification (Price Cleanup)
- **[MODIFY]** [RiwayatRepository.java](file:///D:/lesson/project/iseng/app/src/main/java/com/example/iseng/RiwayatRepository.java):
    - Add `public static final long HARGA_KETRING = 20000;`.
    - Delete `getHargaDefault` and `updateHargaDefault` methods.
- **[MODIFY]** [CatatKetringMassalActivity.java](file:///D:/lesson/project/iseng/app/src/main/java/com/example/iseng/CatatKetringMassalActivity.java):
    - Remove Firebase listener for price.
    - Directly use `RiwayatRepository.HARGA_KETRING`.
- **[MODIFY]** [DetailSiswaActivity.java](file:///D:/lesson/project/iseng/app/src/main/java/com/example/iseng/DetailSiswaActivity.java):
    - Remove Firebase listener for price.
    - Directly use `RiwayatRepository.HARGA_KETRING`.
- **[DELETE]** `PengaturanActivity.java` and `activity_pengaturan.xml`.
- **[MODIFY]** [AndroidManifest.xml](file:///D:/lesson/project/iseng/app/src/main/AndroidManifest.xml): Remove the `<activity>` entry for `PengaturanActivity`.

### 2. Dashboard UI Restructuring (`activity_main.xml`)
- **Spacing & Layout**:
    - Set horizontal padding to `16dp` for the main `LinearLayout`.
    - Standardize vertical margins between sections to `12dp`.
- **Menu Grid**:
    - Replace the current 2+1 grid with a consistent row/grid of 3 vertical cards: **Data Murid**, **Catat Ketring**, **Laporan**.
    - Each card will follow a vertical pattern: `Badge (Icon)` -> `TextView (Label)`.
    - Navigates to `DaftarSiswaActivity`, `CatatKetringMassalActivity`, and `LaporanActivity` respectively.
- **Rekap Jurusan Card**:
    - Rename from "Laporan belum lunas" to "Total per Jurusan".
    - Change background to a darker maroon (`#4A0620`).
    - Fix the data loading issue (ensure `RiwayatRepository` properly triggers the callback and handle potential `null` topJurusans).

### 3. Visual Assets & Theming
- **[NEW/MODIFY]** [bg_icon_badge.xml](file:///D:/lesson/project/iseng/app/src/main/res/drawable/bg_icon_badge.xml): Circular background with `#33C9A227` (20% opacity muted gold).
- **[NEW]** [bg_card_rekap.xml](file:///D:/lesson/project/iseng/app/src/main/res/drawable/bg_card_rekap.xml): Dark maroon rounded background (`#4A0620`, 20dp radius).
- **Icon Tints**:
    - Update all `ImageView` tints to `#C9A227` (muted gold).
    - Includes `btnLogout`, back buttons, and all menu icons.
- **Text Colors**:
    - Labels ("Total Murid", "Belum Lunas", Menu Labels, Rekap Title) -> `#FFD484`.
    - Data Content (Numbers, List rows) -> `#F3E6D8` or white.

### 4. Logic & Reliability
- **[MODIFY]** [MainActivity.java](file:///D:/lesson/project/iseng/app/src/main/java/com/example/iseng/MainActivity.java):
    - Update `populateLaporanPreview` to render the new row layout with Jurusan name (left) and Count (right) with a thin white divider.
    - Add `Log.e` in `RiwayatRepository` for Firebase failures.

## Verification Plan

### Automated Tests
- Build the project to verify all file deletions and code references are updated.
- Verify through Logcat that dashboard data query completes successfully.

### Manual Verification
- **Menu**: Confirm all 3 cards are identical in size/style and icons are stacked above text.
- **Colors**: Visually match tints against the specified hex codes (`#C9A227`, `#FFD484`, `#4A0620`).
- **Functionality**: Record a catering entry and verify the price used is exactly 20.000.
- **Laporan**: Open the new "Laporan" menu and verify it shows the student list correctly.
- **Rekap**: Confirm the "Total per Jurusan" card displays data correctly instead of "Memuat data...".
