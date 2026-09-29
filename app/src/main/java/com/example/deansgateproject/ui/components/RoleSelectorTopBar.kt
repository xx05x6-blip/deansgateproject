package com.example.deansgateproject.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deansgateproject.data.model.ApartmentAddress
import com.example.deansgateproject.data.model.UserRole
import com.example.deansgateproject.ui.theme.CardBorderColor
import com.example.deansgateproject.ui.theme.DarkVioletSurface
import com.example.deansgateproject.ui.theme.DarkVioletSurfaceVariant
import com.example.deansgateproject.ui.theme.DeansgateProjectTheme
import com.example.deansgateproject.ui.theme.PillActiveContainer
import com.example.deansgateproject.ui.theme.PillInactiveContainer
import com.example.deansgateproject.ui.theme.TextMuted
import com.example.deansgateproject.ui.theme.TextSubtitle
import com.example.deansgateproject.ui.theme.TextWhite
import com.example.deansgateproject.ui.theme.VibrantVioletLight
import com.example.deansgateproject.ui.theme.VibrantVioletPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectorTopBar(
    currentRole: UserRole,
    activeAddress: ApartmentAddress,
    onRoleSelected: (UserRole) -> Unit,
    onAddressClick: (() -> Unit)? = null,
    activeOrderCount: Int = 0,
    modifier: Modifier = Modifier
) {
    var isRoleMenuExpanded by remember { mutableStateOf(false) }

    val roleBadgeColor by animateColorAsState(
        targetValue = when (currentRole) {
            UserRole.RESIDENT -> Color(0xFF3B82F6) // Royal Blue
            UserRole.RUNNER -> Color(0xFF10B981)   // Emerald Green
            UserRole.ADMIN -> VibrantVioletLight   // Vibrant Violet Accent
        },
        label = "roleBadgeColor"
    )

    Surface(
        tonalElevation = 4.dp,
        color = DarkVioletSurface,
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Deansgate Square",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PillActiveContainer)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "DELIVERY",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextWhite,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        // Address or Role Subtitle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable(enabled = onAddressClick != null) { onAddressClick?.invoke() }
                                .padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Address",
                                tint = VibrantVioletLight,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = activeAddress.formattedAddress,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = TextSubtitle,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    // Role Switcher Pill Button
                    Box {
                        Surface(
                            onClick = { isRoleMenuExpanded = true },
                            shape = RoundedCornerShape(22.dp),
                            color = PillInactiveContainer,
                            border = BorderStroke(1.dp, roleBadgeColor.copy(alpha = 0.6f)),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(roleBadgeColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = getRoleIcon(currentRole),
                                    contentDescription = currentRole.displayName,
                                    tint = roleBadgeColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentRole.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Rounded.ExpandMore,
                                    contentDescription = "Switch Role",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Role Selector Dropdown Menu
                        DropdownMenu(
                            expanded = isRoleMenuExpanded,
                            onDismissRequest = { isRoleMenuExpanded = false },
                            modifier = Modifier
                                .width(280.dp)
                                .background(DarkVioletSurface)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "SELECT ACTIVE ROLE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSubtitle,
                                    letterSpacing = 0.8.sp
                                )
                            }

                            val visibleRoles = if (currentRole == UserRole.RESIDENT) {
                                UserRole.entries.filter { it != UserRole.ADMIN }
                            } else {
                                UserRole.entries
                            }

                            visibleRoles.forEach { role ->
                                val isSelected = role == currentRole
                                val optionColor = when (role) {
                                    UserRole.RESIDENT -> Color(0xFF3B82F6)
                                    UserRole.RUNNER -> Color(0xFF10B981)
                                    UserRole.ADMIN -> VibrantVioletLight
                                }

                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) PillActiveContainer else PillInactiveContainer)
                                            ) {
                                                Icon(
                                                    imageVector = getRoleIcon(role),
                                                    contentDescription = null,
                                                    tint = optionColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = role.displayName,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = TextWhite
                                                )
                                                Text(
                                                    text = when (role) {
                                                        UserRole.RESIDENT -> "Order food & groceries"
                                                        UserRole.RUNNER -> "Deliver tower orders"
                                                        UserRole.ADMIN -> "Manage stores & analytics"
                                                    },
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextMuted
                                                )
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Check,
                                                    contentDescription = "Active",
                                                    tint = optionColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onRoleSelected(role)
                                        isRoleMenuExpanded = false
                                    },
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) DarkVioletSurfaceVariant
                                            else Color.Transparent
                                        )
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkVioletSurface
                )
            )

            // Dynamic Active Banner per Role
            RoleBanner(
                role = currentRole,
                activeOrderCount = activeOrderCount
            )
        }
    }
}

@Composable
fun RoleBanner(
    role: UserRole,
    activeOrderCount: Int,
    modifier: Modifier = Modifier
) {
    val (bgColor, contentColor, bannerText, subText) = when (role) {
        UserRole.RESIDENT -> Quadruple(
            DarkVioletSurfaceVariant,
            TextWhite,
            "Resident Mode • Deansgate Square",
            if (activeOrderCount > 0) "$activeOrderCount active delivery in progress" else "Browse local stores & order directly to your door"
        )
        UserRole.RUNNER -> Quadruple(
            DarkVioletSurfaceVariant,
            TextWhite,
            "Concierge Delivery Runner Mode",
            if (activeOrderCount > 0) "$activeOrderCount orders pending collection/delivery" else "Ready to accept local tower delivery orders"
        )
        UserRole.ADMIN -> Quadruple(
            DarkVioletSurfaceVariant,
            TextWhite,
            "Operations Control & Analytics Admin",
            "Manage store catalogs, stock availability & live spend analytics"
        )
    }

    Surface(
        color = bgColor,
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = getRoleIcon(role),
                contentDescription = null,
                tint = VibrantVioletLight,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bannerText,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.5.sp,
                    color = TextSubtitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

private fun getRoleIcon(role: UserRole): ImageVector {
    return when (role) {
        UserRole.RESIDENT -> Icons.Rounded.Home
        UserRole.RUNNER -> Icons.AutoMirrored.Rounded.DirectionsRun
        UserRole.ADMIN -> Icons.Rounded.AdminPanelSettings
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectorTopBarPreview() {
    DeansgateProjectTheme {
        Column {
            RoleSelectorTopBar(
                currentRole = UserRole.RESIDENT,
                activeAddress = ApartmentAddress("South Tower", "1204"),
                onRoleSelected = {},
                activeOrderCount = 2
            )
            Spacer(modifier = Modifier.height(16.dp))
            RoleSelectorTopBar(
                currentRole = UserRole.RUNNER,
                activeAddress = ApartmentAddress("West Tower", "1802"),
                onRoleSelected = {},
                activeOrderCount = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            RoleSelectorTopBar(
                currentRole = UserRole.ADMIN,
                activeAddress = ApartmentAddress("East Tower", "2405"),
                onRoleSelected = {},
                activeOrderCount = 0
            )
        }
    }
}
