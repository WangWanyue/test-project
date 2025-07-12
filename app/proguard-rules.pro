# Custom ProGuard rules for the DrawingApp

# Keep our custom View to prevent it from being removed or renamed
-keep class com.example.drawingapp.DrawingView { *; }

# Keep the main entry activity
-keep class com.example.drawingapp.MainActivity { *; }