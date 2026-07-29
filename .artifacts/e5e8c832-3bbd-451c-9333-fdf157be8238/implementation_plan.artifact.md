# Implementation Plan - Bug Fixes & Layout Refinement

This plan addresses the empty list issue in `LaporanActivity` and improves the layout of `DetailJurusanActivity` items for better readability.

## User Review Required

> [!IMPORTANT]
> - I will add extensive logging to `LaporanActivity` to help diagnose Firebase connection or data processing issues.
> - The "Status" filter logic in `LaporanActivity` will be corrected to match the dropdown options.
> - `DetailJurusanActivity` item layout will be significantly adjusted to separate the student count and the status description into two lines.

## Proposed Changes

### 1. Debug & Fix LaporanActivity

#### [MODIFY] [LaporanActivity.java](file:///C:/Users/ASUS/AndroidStudioProjects/dc-beta/app/src/main/java/com/example/iseng/LaporanActivity.java)
- Add `Log.d` to track data reception from Firebase (`siswa`, `riwayat_ketring`, `riwayat_pembayaran`).
- Add `Log.e` to `onCancelled` to capture permission or database errors.
- Fix filter logic: Change `selStatus.equals("Semua Status")` to `selStatus.equals("Status")` to match the default Spinner item.
- Add `try-catch` block inside `processData()` to catch and log potential `NullPointerException` or other runtime errors during calculation.

### 2. Refine Detail Jurusan Layout

#### [MODIFY] [item_detail_jurusan.xml](file:///C:/Users/ASUS/AndroidStudioProjects/dc-beta/app/src/main/res/layout/item_detail_jurusan.xml)
- Implement the requested two-column, multi-line layout.
- Use `tvJurusanKelas` for the left side.
- Use a vertical `LinearLayout` on the right side:
    - `tvUnpaidCount` (Top): Bold, gold (#D9A441), 16sp.
    - `tvTotalInfo` (Bottom): White transparent (#CCFFFFFF), 12sp.
- Set `paddingVertical="14dp"` and `paddingHorizontal="16dp"`.

#### [MODIFY] [DetailJurusanAdapter.java](file:///C:/Users/ASUS/AndroidStudioProjects/dc-beta/app/src/main/java/com/example/iseng/DetailJurusanAdapter.java)
- Ensure the adapter correctly binds to the updated IDs and layout structure.

## Verification Plan

### Automated Tests
- Build the project to verify no syntax errors were introduced.

### Manual Verification
- Deploy to device/emulator.
- Open `LaporanActivity` and check Logcat for "Data diterima" and "Error" logs.
- Verify if the list now populates correctly after fixing the filter logic.
- Open `DetailJurusanActivity` and verify the new card layout matches the requested spacing and font sizes.
