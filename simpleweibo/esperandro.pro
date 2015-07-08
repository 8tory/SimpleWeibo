# esperandro
-keepnames class de.devland.** { *; }
-keep class **$$Impl { public *;}

# keep the annotated things annotated
-keepattributes *Annotation*, EnclosingMethod, Signature, InnerClasses

# for dagger also preserve the interfaces
# assuming they reside in the sub-package 'preferences' and all end with 'Prefs'
#-keep class preferences.**Prefs { public *;}
