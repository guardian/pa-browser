package util

trait ExecutionContexts {
  implicit lazy val executionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext
}
