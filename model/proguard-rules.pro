# 防止DTO和VO被混淆
#-keepclassmembers class com.hua.model.** { *; }
#-keep class com.hua.model.album.AlbumVO
#-keep class com.hua.model.artist.ArtistVO
#-keep class com.hua.model.music.MusicVO
#-keep class com.hua.model.sheet.SheetVO
#-keep class com.hua.model.user.UserVO
# 防止DTO和VO被混淆
-keepclassmembers class com.hua.model.** { *; }