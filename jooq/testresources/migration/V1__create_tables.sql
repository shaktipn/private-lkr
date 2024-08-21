CREATE TABLE "Deal" (
    "dealId" UUID PRIMARY KEY,
    "amount" NUMERIC NOT NULL,
    "dealName" VARCHAR(100) NOT NULL
);

CREATE TABLE "DealRequest" (
    "reqId" UUID PRIMARY KEY,
    "expiresAt" TIMESTAMP NOT NULL,
    "dealDate" TIMESTAMP NOT NULL UNIQUE,
    "dealId" UUID,
    CONSTRAINT fk_dealId FOREIGN KEY ("dealId") REFERENCES "Deal" ("dealId")
);

CREATE TABLE "DealHistory" (
    "histId" UUID PRIMARY KEY,
    "lastUpdate" TIMESTAMP NOT NULL DEFAULT (now()),
    "dealDate" TIMESTAMP NOT NULL,
    "dealId" UUID,
    CONSTRAINT fk_dealId FOREIGN KEY ("dealId") REFERENCES "Deal" ("dealId"), -- Similar name foreign keys.
    CONSTRAINT fk_dealDate FOREIGN KEY ("dealDate") REFERENCES "DealRequest" ("dealDate") -- Foreign key of same name as table name.
);
