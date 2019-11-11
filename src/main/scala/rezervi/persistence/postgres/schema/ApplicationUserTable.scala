package rezervi.persistence.postgres.schema

import scalikejdbc._

object ApplicationUserTable extends SQLSyntaxSupport[Any] {
  override def tableName: String = "application_user"
}
