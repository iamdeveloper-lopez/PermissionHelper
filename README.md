
# PermissionHelper  
PermissionHelper helps developer to easy handle the runtime permissions  

Supports : 
- Fragment / Activity  
- Multiple permissions  
- Listeners for better use    

[![](https://jitpack.io/v/iamdeveloper-lopez/PermissionHelper.svg)](https://jitpack.io/#iamdeveloper-lopez/PermissionHelper)    

## Gradle  
```  
allprojects {  
 repositories { 
	 ... 
	 maven { url 'https://jitpack.io' } 	
	}
}  
```  
```  
dependencies {  
	 implementation 'com.github.iamdeveloper-lopez:PermissionHelper:0.0.2'
 }  
```  
  
## Implementation  

Declare PermissionHelper
```    
private PermissionHelper permissionHelper;  
```  

Create a builder  

```
PermissionHelper.Builder builder = new PermissionHelper.Builder(this/* Activity / Fragment */)/This permissionhelper supports Fragment/Activity, just past it  
 .addPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
 Manifest.permission.ACCESS_FINE_LOCATION, 
 Manifest.permission.WRITE_EXTERNAL_STORAGE, 
 Manifest.permission.READ_EXTERNAL_STORAGE)//Add all permissions you want to check with runtime 
.requestCode(1000)//provide request code for checking later on onRequestPermissionsResult method
.setPermissionListener((PermissionHelper.RequestPermissionRationaleListener) permissionsNeedRationale -> { 
	 //All permissions that need rationale //Do Something 
 }) 
 .setPermissionListener(new PermissionHelper.RequestPermissionListener() { 
	 @Override 
	 public void permissionsGranted() { 
		 //All permissions granted //Do Something 
	 }  
	 @Override 
	 public void permissionsDenied() { 
		 permissionHelper.check();//if permission is denied you can put checker here so that RequestPermissionRationaleListener will call 
	 } 
 }); 
 permissionHelper = builder.build();//Build the builder 
 permissionHelper.check();//This will check permission and popup an runtime permission  
 ```
Inside onRequestPermissionsResult pass all values to PermissionHelper to check for the permissions by request code you've provided  
```
@Override  
 public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { 
	 super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	 permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults); 
 }
```
# License  
Copyright 2018 Lester Lopez  
  
Licensed under the Apache License, Version 2.0 (the "License");  
you may not use this file except in compliance with the License.  
You may obtain a copy of the License at  
  
 http://www.apache.org/licenses/LICENSE-2.0  
Unless required by applicable law or agreed to in writing, software  
distributed under the License is distributed on an "AS IS" BASIS,  
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
See the License for the specific language governing permissions and  
limitations under the License.
