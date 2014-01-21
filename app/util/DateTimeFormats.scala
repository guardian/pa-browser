package util

import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object DateTimeFormats {
  val dateTime = DateTimeFormat.forPattern("HH:mm dd MMMM yyyy")
}
