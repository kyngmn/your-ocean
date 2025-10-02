import { Kysely, PostgresDialect } from "kysely"

import type { DB } from "@/types/schema"
import { Pool } from "pg"

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  max: 10
})

export const db = new Kysely<DB>({
  dialect: new PostgresDialect({
    pool
  })
})
