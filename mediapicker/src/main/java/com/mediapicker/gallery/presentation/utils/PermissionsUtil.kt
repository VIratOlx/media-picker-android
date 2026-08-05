package com.mediapicker.gallery.presentation.utils

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.olx.permify.Permify
import com.olx.permify.callback.PermanentPermissionDeniedCallback
import com.olx.permify.callback.PermissionDeniedCallback
import com.olx.permify.callback.PermissionRequestCallback

object PermissionsUtil {

    fun requiredPermissions(): List<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
            Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            else -> listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun isMediaAccessGranted(context: Context): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                Permify.isPermissionGranted(
                    context,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) -> true
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                Permify.isPermissionGranted(context, Manifest.permission.READ_MEDIA_IMAGES) ||
                    Permify.isPermissionGranted(context, Manifest.permission.READ_MEDIA_VIDEO)
            else ->
                Permify.isPermissionGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    fun requestMediaPermissions(
        fragment: Fragment,
        onGranted: () -> Unit,
        onDenied: () -> Unit,
        onPermanentlyDenied: () -> Unit
    ) {
        val context = fragment.requireContext()

        Permify.requestPermission(
            fragment = fragment,
            permissions = requiredPermissions(),
            showDialogs = false,
            permissionRequestCallback = object : PermissionRequestCallback {
                override fun onResult(
                    allGranted: Boolean,
                    grantedList: List<String>,
                    deniedList: List<String>
                ) {
                    if (allGranted || isMediaAccessGranted(context)) {
                        onGranted()
                    }
                }
            },
            permissionDeniedCallback = object : PermissionDeniedCallback {
                override fun onPermissionDenied(permissionDeniedList: List<String>) {
                    if (!isMediaAccessGranted(context)) {
                        onDenied()
                    }
                }
            },
            permanentPermissionDeniedCallback = object : PermanentPermissionDeniedCallback {
                override fun onPermanentPermissionDenied(permanentPermissionDenied: List<String>) {
                    if (!isMediaAccessGranted(context)) {
                        onPermanentlyDenied()
                    }
                }
            }
        )
    }
}
