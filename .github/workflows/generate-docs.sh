#!/bin/bash

set -e

# Create pages directory
mkdir -p pages

# Copy existing documentation
if [ -d "docs" ]; then
  cp -r docs/* pages/
fi

# Copy generated user manual if it exists
if [ -d "target/documentation" ]; then
  cp -r target/documentation/* pages/
fi

# Create main index.html
cat > pages/index.html << EOF
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dokumentasi - Yayasan Sahabat Quran</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gradient-to-br from-green-600 to-green-800 min-h-screen">
    <div class="container mx-auto px-4 py-8">
        <div class="max-w-4xl mx-auto">
            <header class="text-center mb-12">
                <h1 class="text-4xl md:text-6xl font-bold text-white mb-4">
                    📖 Dokumentasi
                </h1>
                <h2 class="text-2xl md:text-3xl text-green-100 mb-2">
                    Yayasan Sahabat Quran
                </h2>
                <p class="text-green-200">
                    Sistem Manajemen Pembelajaran Al-Quran
                </p>
            </header>
            
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <!-- User Manual -->
                <div class="bg-white rounded-lg shadow-xl p-6 transform hover:scale-105 transition">
                    <div class="text-4xl mb-4">👥</div>
                    <h3 class="text-xl font-bold mb-2">Panduan Pengguna</h3>
                    <p class="text-gray-600 mb-4">
                        Panduan lengkap 3 tahap proses pendaftaran siswa
                    </p>
                    <a href="PANDUAN_PENGGUNA.html" 
                       class="inline-block bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 transition">
                        Baca Panduan →
                    </a>
                </div>
                
                <!-- Technical Documentation -->
                <div class="bg-white rounded-lg shadow-xl p-6 transform hover:scale-105 transition">
                    <div class="text-4xl mb-4">⚙️</div>
                    <h3 class="text-xl font-bold mb-2">Dokumentasi Teknis</h3>
                    <p class="text-gray-600 mb-4">
                        Fitur, implementasi, dan panduan pengembangan
                    </p>
                    <div class="space-y-2">
                        <a href="README.html" class="block text-green-600 hover:text-green-800">• README</a>
                        <a href="FEATURES.html" class="block text-green-600 hover:text-green-800">• Features</a>
                        <a href="TESTING.html" class="block text-green-600 hover:text-green-800">• Testing Guide</a>
                        <a href="SECURITY.html" class="block text-green-600 hover:text-green-800">• Security</a>
                    </div>
                </div>
                
                <!-- Implementation Progress -->
                <div class="bg-white rounded-lg shadow-xl p-6 transform hover:scale-105 transition">
                    <div class="text-4xl mb-4">📊</div>
                    <h3 class="text-xl font-bold mb-2">Progress Implementasi</h3>
                    <p class="text-gray-600 mb-4">
                        Status implementasi fitur dan roadmap
                    </p>
                    <a href="IMPLEMENTATION_PROGRESS.html" 
                       class="inline-block bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition">
                        Lihat Progress →
                    </a>
                </div>
                
                <!-- User Manual Generation -->
                <div class="bg-white rounded-lg shadow-xl p-6 transform hover:scale-105 transition">
                    <div class="text-4xl mb-4">🤖</div>
                    <h3 class="text-xl font-bold mb-2">Generasi Manual</h3>
                    <p class="text-gray-600 mb-4">
                        Sistem otomatis generate panduan pengguna
                    </p>
                    <a href="USER_MANUAL_GENERATION.html" 
                       class="inline-block bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700 transition">
                        Pelajari Sistem →
                    </a>
                </div>
                
                <!-- Class Workflow -->
                <div class="bg-white rounded-lg shadow-xl p-6 transform hover:scale-105 transition">
                    <div class="text-4xl mb-4">🎯</div>
                    <h3 class="text-xl font-bold mb-2">Workflow Kelas</h3>
                    <p class="text-gray-600 mb-4">
                        Alur kerja persiapan dan manajemen kelas
                    </p>
                    <a href="planning/CLASS_PREPARATION_WORKFLOW.html" 
                       class="inline-block bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700 transition">
                        Lihat Workflow →
                    </a>
                </div>
                
                <!-- GitHub Repository -->
                <div class="bg-white rounded-lg shadow-xl p-6 transform hover:scale-105 transition">
                    <div class="text-4xl mb-4">💻</div>
                    <h3 class="text-xl font-bold mb-2">Source Code</h3>
                    <p class="text-gray-600 mb-4">
                        Repository GitHub dan kontribusi
                    </p>
                    <a href="https://github.com/endymuhardin/sahabat-quran" 
                       target="_blank"
                       class="inline-block bg-gray-800 text-white px-4 py-2 rounded hover:bg-gray-900 transition">
                        GitHub Repository →
                    </a>
                </div>
            </div>
            
            <footer class="text-center mt-12 text-green-200">
                <p>&copy; 2024 Yayasan Sahabat Quran</p>
                <p class="text-sm mt-2">
                    Dokumentasi diperbarui otomatis • 
                    <span class="text-green-300">Last updated: $(date)</span>
                </p>
            </footer>
        </div>
    </div>
</body>
</html>
EOF

# Convert markdown files to HTML using a simple approach
find pages -name "*.md" | while read -r file;
do
    if command -v pandoc >/dev/null 2>&1;
    then
        html_file="${file%.md}.html"
        pandoc -f markdown -t html5 \
          --standalone \
          --metadata title="$(basename "${file%.md}") - Yayasan Sahabat Quran" \
          --css "https://cdn.jsdelivr.net/npm/github-markdown-css@5/github-markdown-light.css" \
          --css "data:text/css,body{box-sizing:border-box;min-width:200px;max-width:980px;margin:0 auto;padding:45px;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif}.markdown-body{font-size:16px;line-height:1.6}.markdown-body img{max-width:100%;height:auto;border-radius:8px;box-shadow:0 4px 6px rgba(0,0,0,0.1)}.markdown-body h1,.markdown-body h2{color:#047857;border-bottom:2px solid #d1fae5}.markdown-body a{color:#059669}" \
          "$file" \
          -o "$html_file"
    else
        # Fallback: create simple HTML wrapper for markdown
        html_file="${file%.md}.html"
        cat > "$html_file" << EOF
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$(basename "${file%.md}") - Yayasan Sahabat Quran</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; max-width: 980px; margin: 0 auto; padding: 45px; }
        pre { background: #f6f8fa; padding: 16px; border-radius: 8px; overflow-x: auto; }
        code { background: #f6f8fa; padding: 2px 4px; border-radius: 4px; }
        img { max-width: 100%; height: auto; border-radius: 8px; }
        h1, h2 { color: #047857; border-bottom: 2px solid #d1fae5; padding-bottom: 8px; }
        a { color: #059669; }
    </style>
</head>
<body>
    <div style="background: #f0fdf4; padding: 1rem; border-radius: 8px; margin-bottom: 2rem;">
        <strong>📝 Catatan</strong> File markdown ini akan ditampilkan dengan format HTML sederhana.
        <a href="$file" style="margin-left: 1rem;">Lihat file asli →</a>
    </div>
    <pre style="white-space: pre-wrap; font-family: inherit;">$(cat "$file")</pre>
</body>
</html>
EOF
    fi
done

echo "=== Documentation Site Structure ==="
find pages -type f | head -20
