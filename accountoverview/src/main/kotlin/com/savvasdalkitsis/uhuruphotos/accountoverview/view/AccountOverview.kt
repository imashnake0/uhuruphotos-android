package com.savvasdalkitsis.uhuruphotos.accountoverview.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIcon
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.UserBadge
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

@Composable
fun AccountOverview(
    userInformationState: UserInformationState,
    onLogoutClicked: () -> Unit,
    onEditServerClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = 120.dp)
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            UserBadge(
                state = userInformationState,
                size = 48.dp
            )
            Column {
                Text(
                    text = userInformationState.userFullName,
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = userInformationState.serverUrl,
                    style = MaterialTheme.typography.caption.copy(color = Color.Gray),
                )
            }
            ActionIcon(
                onClick = onEditServerClicked,
                icon = R.drawable.ic_edit,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onSettingsClicked
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Settings")
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onLogoutClicked,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Log out")
            }
        }
    }
}