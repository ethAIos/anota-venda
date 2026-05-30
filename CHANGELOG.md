# Changelog

All notable changes to **Caderninho** are documented in this file.

## [1.0.2] - 2026-05-30

### Changed

- Unified UI components through shared `CaderninhoUiStandards` (buttons, toggles, steppers, chips, list rows).
- Refreshed layouts across customer, order, settings, onboarding, and “paying today” screens for consistent spacing and typography.
- Date picker now converts dates in UTC so the selected civil date stays correct across time zones.
- Widget refresh is deferred 3 seconds after app start to keep cold start responsive on slow devices and software emulators.

### Added

- Unit tests for date picker date round-trip (`DatePickerDatesTest`).
- Device QA script for date picker verification (`scripts/verify-date-picker-device-qa.ps1`).
- Cursor Cloud / dev environment docs (`AGENTS.md`) and Android verification scripts (`scripts/verify-android.sh`, emulator helpers).

### Fixed

- Widget initialization no longer runs synchronously at process start, reducing ANR risk on low-end hardware.

## [1.0.0] - 2026-05-27

### Added

- Initial release: offline sales notebook for customers, orders, installments, and “paying today” collections.
- Home screen widgets (A1, A2, A3) for quick balances and due installments.
- WhatsApp integration for payment reminders.
- Local persistence with Room and DataStore.

[1.0.2]: https://github.com/ethAIos/anota-venda/releases/tag/v1.0.2
[1.0.0]: https://github.com/ethAIos/anota-venda/releases/tag/v1.0.0
