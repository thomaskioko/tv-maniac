package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ColumnSpacer(value: Int) = Spacer(modifier = Modifier.height(value.dp))

@Suppress("unused")
@Composable
fun RowSpacer(value: Int) = Spacer(modifier = Modifier.width(value.dp))
