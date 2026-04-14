

CREATE TABLE users (
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL
);

CREATE TABLE rooms (
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    created_by  UUID REFERENCES users(id),
    created_at  TIMESTAMPTZ NOT NULL
);

CREATE TABLE room_members (
    room_id    UUID REFERENCES rooms(id),
    user_id    UUID REFERENCES users(id),
    role       VARCHAR(10) NOT NULL,
    joined_at  TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (room_id, user_id)
);

CREATE TABLE tasks (
    id          UUID PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    difficulty  VARCHAR(10) NOT NULL,
    category    VARCHAR(50),
    room_id     UUID REFERENCES rooms(id),
    created_by  UUID REFERENCES users(id),
    created_at  TIMESTAMPTZ NOT NULL,
    is_active   BOOLEAN DEFAULT true
);

CREATE TABLE task_events (
    id          UUID PRIMARY KEY,
    task_id     UUID REFERENCES tasks(id),
    user_id     UUID REFERENCES users(id),
    room_id     UUID REFERENCES rooms(id),
    status      VARCHAR(20) NOT NULL,
    comment     TEXT,
    occurred_at TIMESTAMPTZ NOT NULL
);