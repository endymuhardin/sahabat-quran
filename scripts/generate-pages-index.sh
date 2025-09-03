#!/bin/bash

# Generate index.html files for GitHub Pages documentation

set -e

# Create individual index files for each documentation section
for test_dir in _site/*/; do
  if [ -d "$test_dir" ]; then
    test_name=$(basename "$test_dir")
    
    # Find the latest documentation session for this test
    latest_session=$(find "$test_dir" -name "documentation-session.json" -exec dirname {} \; | head -1)
    
    if [ -n "$latest_session" ]; then
      # Create index.html for this test section
      case "$test_name" in
        "StudentRegistrationUserGuideTest")
          title="🎓 Panduan Registrasi Siswa"
          description="Panduan lengkap untuk proses registrasi siswa baru, termasuk pengisian formulir dan tes penempatan."
          ;;
        "StaffRegistrationUserGuideTest")
          title="👥 Panduan Registrasi Staf"
          description="Dokumentasi untuk admin akademik mengelola registrasi dan penugasan evaluasi guru."
          ;;
        "TeacherRegistrationUserGuideTest")
          title="🍎 Panduan Registrasi Guru"
          description="Panduan untuk guru dalam proses registrasi dan manajemen profil pengajar."
          ;;
        *)
          title="$test_name"
          description="Dokumentasi user guide."
          ;;
      esac
      
      # Find all screenshots and videos in the latest session
      screenshots=$(find "$latest_session" -name "*.png" | sort)
      videos=$(find "$latest_session" -name "*.webm")
      
      cat > "$test_dir/index.html" << 'EOF'
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TITLE_PLACEHOLDER</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }
        .nav-links { margin: 20px 0; }
        .nav-links a { background: #3498db; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-right: 10px; }
        .nav-links a:hover { background: #2980b9; }
        .video-section { margin: 30px 0; }
        .screenshots-section { margin: 30px 0; }
        .screenshot-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin: 20px 0; }
        .screenshot-item { border: 1px solid #ddd; border-radius: 5px; padding: 10px; background: #f9f9f9; }
        .screenshot-item img { max-width: 100%; height: auto; border-radius: 3px; }
        .screenshot-title { font-weight: bold; margin-bottom: 5px; color: #2c3e50; }
        video { max-width: 100%; height: auto; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>TITLE_PLACEHOLDER</h1>
        <p>DESCRIPTION_PLACEHOLDER</p>
        
        <div class="nav-links">
            <a href="../index.html">← Kembali ke Beranda</a>
        </div>
EOF
      
      # Replace placeholders
      sed -i "s/TITLE_PLACEHOLDER/$title/g" "$test_dir/index.html"
      sed -i "s/DESCRIPTION_PLACEHOLDER/$description/g" "$test_dir/index.html"

      if [ -n "$videos" ]; then
        echo '<div class="video-section"><h2>🎥 Video Panduan</h2>' >> "$test_dir/index.html"
        for video in $videos; do
          relative_video=${video#$test_dir}
          echo "<video controls><source src=\"$relative_video\" type=\"video/webm\">Browser Anda tidak mendukung video HTML5.</video>" >> "$test_dir/index.html"
        done
        echo '</div>' >> "$test_dir/index.html"
      fi

      if [ -n "$screenshots" ]; then
        echo '<div class="screenshots-section"><h2>📸 Screenshot Panduan</h2><div class="screenshot-grid">' >> "$test_dir/index.html"
        for screenshot in $screenshots; do
          relative_screenshot=${screenshot#$test_dir}
          filename=$(basename "$screenshot")
          # Extract step number and description from filename
          step_desc=$(echo "$filename" | sed 's/^[0-9]*_//' | sed 's/\.png$//' | sed 's/_/ /g')
          echo "<div class=\"screenshot-item\"><div class=\"screenshot-title\">$step_desc</div><img src=\"$relative_screenshot\" alt=\"$step_desc\"></div>" >> "$test_dir/index.html"
        done
        echo '</div></div>' >> "$test_dir/index.html"
      fi

      cat >> "$test_dir/index.html" << 'EOF'
    </div>
</body>
</html>
EOF
    fi
  fi
done