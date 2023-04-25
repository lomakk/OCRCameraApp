package com.vision.scantexter.android.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import com.vision.scantexter.android.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermission(
    permissions: List<String> = listOf(Manifest.permission.ACCESS_FINE_LOCATION),
    @StringRes messageResId: Int,
    permissionDenied: () -> Unit,
    permissionConfirmed: (() -> Unit)? = null,
    content: @Composable (() -> Unit)
) {

    val context = LocalContext.current

    // Track if the user doesn't want to see the rationale any more.
    val doNotShowRationale = rememberSaveable { mutableStateOf(false) }

    val permissionState = rememberMultiplePermissionsState(permissions)

    PermissionsRequired(
        multiplePermissionsState = permissionState,
        permissionsNotGrantedContent = {
            if (doNotShowRationale.value) {
                LaunchedEffect(doNotShowRationale.value) {
                    permissionDenied()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(MaterialTheme.colors.background),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(messageResId),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(
                            modifier = Modifier.width(150.dp),
                            onClick = { permissionState.launchMultiplePermissionRequest() }
                        ) {
                            Text(stringResource(R.string.permission_grant))
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            modifier = Modifier.width(150.dp),
                            onClick = { doNotShowRationale.value = true }
                        ) {
                            Text(stringResource(R.string.permission_denied))
                        }
                    }
                }
            }
        },
        permissionsNotAvailableContent = {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.permission_denied_message))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    Intent(
                        ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")
                    ).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(this)
                    }
                }) {
                    Text(stringResource(R.string.permission_go_to_settings))
                }
            }
        }
    ) {
        LaunchedEffect(doNotShowRationale.value) {
            permissionConfirmed?.let { it() }
        }
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun RequestLocationPermissionPreview() {
    RequestPermission(permissionDenied = {}, messageResId = R.string.permission_denied_message) {

    }
}