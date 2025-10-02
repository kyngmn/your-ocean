import type { Updateable } from "kysely"
import type { Users } from "@/types/schema"
import { db } from "@/lib/db"

export class UserRepository {
  async findById(id: number) {
    return await db
      .selectFrom("users")
      .selectAll()
      .where("id", "=", id)
      .where("deletedAt", "is", null)
      .executeTakeFirst()
  }

  async findByEmail(email: string) {
    return await db
      .selectFrom("users")
      .selectAll()
      .where("email", "=", email)
      .where("deletedAt", "is", null)
      .executeTakeFirst()
  }

  async findAll() {
    return await db
      .selectFrom("users")
      .selectAll()
      .where("deletedAt", "is", null)
      .execute()
  }

  async create(data: Omit<Users, "id" | "createdAt" | "updatedAt" | "deletedAt" | "aiStatus">) {
    return await db
      .insertInto("users")
      .values(data)
      .returningAll()
      .executeTakeFirstOrThrow()
  }

  async update(id: number, data: Partial<Updateable<Users>>) {
    return await db
      .updateTable("users")
      .set({
        ...data,
        updatedAt: new Date()
      })
      .where("id", "=", id)
      .where("deletedAt", "is", null)
      .returningAll()
      .executeTakeFirst()
  }

  async softDelete(id: number) {
    return await db
      .updateTable("users")
      .set({
        deletedAt: new Date(),
        updatedAt: new Date()
      })
      .where("id", "=", id)
      .returningAll()
      .executeTakeFirst()
  }

  async getUserWithPersonas(userId: number) {
    return await db
      .selectFrom("users")
      .leftJoin("userPersonas", "users.id", "userPersonas.userId")
      .select([
        "users.id",
        "users.email",
        "users.nickname",
        "users.profileImageUrl",
        "users.aiStatus",
        "userPersonas.id as personaId",
        "userPersonas.personaCode",
        "userPersonas.nickname as personaNickname"
      ])
      .where("users.id", "=", userId)
      .where("users.deletedAt", "is", null)
      .execute()
  }
}
