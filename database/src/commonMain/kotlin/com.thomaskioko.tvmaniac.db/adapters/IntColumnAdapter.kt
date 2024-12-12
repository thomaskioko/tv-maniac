package com.thomaskioko.tvmaniac.db.adapters

import app.cash.sqldelight.ColumnAdapter

val intColumnAdapter =
  object : ColumnAdapter<List<Int>, String> {

    override fun encode(value: List<Int>) = value.joinToString(separator = ",")

    override fun decode(databaseValue: String): List<Int> =
      if (databaseValue.isEmpty()) {
        listOf()
      } else {
        databaseValue.split(",").map { it.toInt() }
      }
  }
