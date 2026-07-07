# Protocol Simulator Web Design

## Background

`solution-simulator` is currently focused on backend-side simulator capabilities, with `video-osd-simulator` already exposing a WebSocket stream at `/ws/osd` for frontend debugging and integration. There is no dedicated frontend module for protocol-oriented debugging, simulation, or message inspection.

The goal of this design is to add a reusable frontend workspace under `solution-simulator` that can host multiple protocol simulator pages over time. The first delivered page will be a WebSocket simulator and message monitor.

## Goals

- Add a dedicated frontend module under `solution-simulator` for protocol simulator pages.
- Make the module independently runnable for local development without depending on a Spring Boot static resource mount.
- Deliver a first WebSocket page that supports connection, message monitoring, and keyword-based fuzzy filtering.
- Keep the first version lightweight and easy to evolve toward additional protocol pages such as MQTT, HTTP, and TCP.

## Non-Goals

- No backend listener creation from the browser. The page will connect to an existing `ws://` or `wss://` endpoint as a WebSocket client.
- No full protocol mocking platform in the first version.
- No user login, persistence, or remote collaboration capability.
- No framework-heavy frontend stack for the first version beyond Vite and browser-native JavaScript.

## Recommended Approach

Three approaches were considered:

1. `Vite + native JavaScript`
   - Lowest setup and maintenance cost.
   - Enough structure for a multi-page protocol tool if code is organized by feature.
   - Recommended for this repository because `solution-hub` values readable, portable examples.

2. `Vite + Vue`
   - Better component ergonomics and richer UI scaling.
   - Higher initial cost and additional framework weight for a first protocol page.

3. `Vite + React`
   - Strong ecosystem and mature patterns.
   - Similar cost to Vue without a clear repository-level need today.

The chosen approach is `Vite + native JavaScript`.

## Module Placement

The new module will be created at:

`solution-simulator/protocol-simulator-web`

This keeps the frontend tool inside the simulator topic, while remaining independent from `video-osd-simulator` runtime packaging.

## Module Structure

```text
solution-simulator/protocol-simulator-web
├─ package.json
├─ README.md
├─ index.html
├─ vite.config.js
├─ public/
└─ src/
   ├─ main.js
   ├─ styles/
   │  └─ app.css
   ├─ app/
   │  ├─ layout.js
   │  ├─ router.js
   │  └─ store.js
   ├─ shared/
   │  ├─ components/
   │  └─ utils/
   └─ features/
      ├─ home/
      │  └─ home-page.js
      └─ websocket/
         ├─ websocket-page.js
         ├─ websocket-client.js
         ├─ websocket-log-store.js
         └─ websocket-filter.js
```

## Information Architecture

The UI will start with two views:

1. Home view
   - Shows the protocol simulator workspace title.
   - Shows available protocol entries as compact tool tiles.
   - Initially exposes only the WebSocket entry.
   - Leaves room for future MQTT, HTTP, TCP, or custom protocol pages.

2. WebSocket view
   - Primary work surface for connection setup and message inspection.
   - Structured for repeated debugging use rather than demo-only display.

## WebSocket View Design

The WebSocket page will contain three main regions:

1. Connection panel
   - WebSocket URL input for `ws://` or `wss://`.
   - Connect and disconnect actions.
   - Auto-reconnect toggle.
   - Live connection state badge showing `idle`, `connecting`, `open`, `closed`, or `error`.

2. Toolbar
   - Keyword filter input.
   - Clear log action.
   - Auto-scroll pause/resume action.
   - Export visible log action.

3. Message stream
   - Scrollable message list.
   - Timestamp per message.
   - Event type indicator such as `message`, `open`, `close`, or `error`.
   - Full payload rendering with wrapping for long content.
   - Copy action per entry.

## Interaction Model

- The user enters a WebSocket URL and clicks connect.
- The page creates a browser WebSocket client connection to that endpoint.
- System events and incoming messages are appended to an in-memory log store.
- The keyword filter performs case-insensitive fuzzy matching against the full visible text of each log entry.
- Filtering affects only the rendered list, not the underlying stored log entries.
- Auto-scroll is enabled by default and can be paused by the user.
- Clearing logs removes current in-memory entries and resets the visible stream.

## Data Flow

Responsibilities are intentionally separated:

- `websocket-client.js`
  - Owns the browser `WebSocket` instance.
  - Converts browser socket callbacks into normalized events.

- `websocket-log-store.js`
  - Stores log entries in memory.
  - Tracks filter text and UI state such as auto-scroll.
  - Produces filtered entries for rendering.

- `websocket-filter.js`
  - Implements the case-insensitive fuzzy matching rule.
  - Remains pure and testable without DOM dependencies.

- `websocket-page.js`
  - Binds form controls, status rendering, toolbar actions, and message list updates.

- `layout.js` and `router.js`
  - Provide a stable shell so later protocol pages reuse navigation and page framing.

## Error Handling

The WebSocket page should handle the following gracefully:

- Empty or malformed WebSocket URL before connection attempt.
- Browser connection failure.
- Server-side close events with code and reason when available.
- Message payloads that are large or not JSON.
- Reconnect toggle being enabled while the endpoint remains unavailable.

Errors will be surfaced as visible log entries and status changes rather than modal interruptions.

## Testing Strategy

The first version will use focused tests scaled to the change:

- Unit test for the fuzzy filter logic in `websocket-filter.js`.
- Unit test for log-store filtering behavior if the store grows beyond trivial logic.
- Manual verification against a real endpoint, with `ws://127.0.0.1:18083/ws/osd` as the default local validation target when available.

Manual verification checklist:

- Connect successfully to a reachable endpoint.
- Show state transitions for connect and disconnect.
- Receive and render messages continuously.
- Filter messages with mixed-case keywords.
- Clear logs.
- Pause auto-scroll and confirm the list does not jump.
- Export visible log entries.

## Build and Run

The module will be developed as a standard Vite application:

- Local development via `npm install` and `npm run dev`
- Production build via `npm run build`

This keeps the simulator frontend independently runnable while preserving the option to embed build artifacts elsewhere later.

## Documentation

The module README should include:

- Purpose of the module
- Supported first-page capabilities
- Local run commands
- Suggested validation target using `/ws/osd`
- Notes on how to add future protocol pages

## Future Evolution

The structure is intentionally prepared for additional protocol simulators:

- MQTT page with broker connection and topic message view
- HTTP mock/debug page with request builder and response panel
- TCP raw message page if later paired with a browser-compatible bridge

Any future page should reuse the existing application shell and shared log-display patterns rather than inventing a new UI shape.

## Scope Decision

This design covers one implementation slice:

- scaffold `protocol-simulator-web`
- build the shared app shell
- deliver the first WebSocket page
- add the minimum test coverage for fuzzy filtering

This is intentionally small enough for one implementation plan and leaves protocol expansion to later iterations.
