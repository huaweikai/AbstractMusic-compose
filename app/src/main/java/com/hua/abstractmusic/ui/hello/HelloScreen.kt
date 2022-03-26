package com.hua.abstractmusic.ui.hello

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.ui.hello.PermissionBean
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.route.Screen


/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 第一次打开的应用介绍
 */
@Composable
fun HelloScreen(
    appNavController: NavHostController = LocalAppNavController.current
) {
    var isGet by remember {
        mutableStateOf(true)
    }
    val permissionGet = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            it.forEach { map ->
                if (!map.value) {
                    isGet = false
                }
            }
            if (isGet) {
                val navOptions =
                    NavOptions.Builder().setPopUpTo(Screen.HelloScreen.route, true).build()
//                viewModel.refresh()
                appNavController.navigate(Screen.HomeScreen.route, navOptions)
            }
        }
    )
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.padding(top = 30.dp))
            Text(
                modifier = Modifier.padding(start = 20.dp),
                text = stringResource(id = R.string.hello_permission_title),
                fontWeight = FontWeight.W400,
                fontSize = 35.sp
            )

            Text(
                text = stringResource(id = R.string.hello_text),
                modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 13.dp),
                fontSize = 18.sp,
                lineHeight = 25.sp
            )
            HelloColumns()
            Column(
                Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                    ),
                    onClick = {
                        permissionGet.launch(
                            arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 35.dp, end = 35.dp, bottom = 40.dp)
                        .align(Alignment.CenterHorizontally),

                    ) {
                    Text(
                        text = stringResource(id = R.string.hello_permission_request_bt),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun HelloColumns() {
    val list = listOf(
        PermissionBean(
            R.drawable.ic_hello_net,
            stringResource(id = R.string.hello_permission_net_title),
            stringResource(id = R.string.hello_permission_net_subtitle)
        ),
        PermissionBean(
            R.drawable.ic_hello_save,
            stringResource(id = R.string.hello_permission_read_title),
            stringResource(id = R.string.hello_permission_read_subtitle)
        ),
        PermissionBean(
            R.drawable.ic_hello_service,
            stringResource(id = R.string.hello_permission_service_title),
            stringResource(id = R.string.hello_permission_service_subtitle)
        )
    )
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 25.dp)
            .fillMaxWidth()
    ) {
        items(list) { it ->
            HelloPermissionItem(permissionBean = it)
        }
    }
}

@Composable
fun HelloPermissionItem(
    permissionBean: PermissionBean
) {
        Column(Modifier.padding(top = 10.dp, bottom = 10.dp)) {
            Row {
                Icon(
                    painter = painterResource(id = permissionBean.img),
                    contentDescription = permissionBean.title,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text(
                    text = permissionBean.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500
                )
            }
            Text(
                text = permissionBean.subtitle,
                modifier = Modifier.padding(top = 5.dp, start = 34.dp)
            )
        }
}