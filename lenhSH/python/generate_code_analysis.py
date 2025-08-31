import os
import sys
import re
import logging
from pathlib import Path
from typing import Dict, Optional, List

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
logger = logging.getLogger(__name__)

# Kiểm tra tham số dòng lệnh
if len(sys.argv) != 2:
    logger.error("Usage: python generate_code_analysis.py <path_to_repository>")
    sys.exit(1)

repo_path = Path(sys.argv[1])
if not repo_path.is_dir():
    logger.error(f"{repo_path} is not a valid directory")
    sys.exit(1)

# Tạo thư mục output
output_dir = repo_path / 'code_analysis_docs'
output_dir.mkdir(exist_ok=True)

# Định nghĩa các loại dự án và phần mở rộng tệp
PROJECT_TYPES = {
    'foundry': ['.sol', '.t.sol', '.s.sol', '.toml', '.yml', '.yaml', '.json', '.md', '.txt'],
    'nextjs': ['.js', '.jsx', '.ts', '.tsx', '.css', '.scss', '.html', '.json', '.mjs', '.md', '.txt', '.yaml', '.yml'],
    'cargo': ['.rs', '.toml', '.yml', '.yaml', '.json', '.md', '.txt', '.lock'],
    'hardhat': ['.sol', '.js', '.ts', '.json', '.md', '.txt']
}

# Các thư mục cần bỏ qua theo loại dự án
EXCLUDE_DIRS = {
    'foundry': ['.git', 'code_analysis_docs', 'out', 'cache', 'node_modules', 'lib'],
    'nextjs': ['.git', 'code_analysis_docs', 'node_modules', '.next', 'dist', 'build', 'public'],
    'cargo': ['.git', 'code_analysis_docs', 'target', 'build'],
    'hardhat': ['.git', 'code_analysis_docs', 'artifacts', 'cache', 'node_modules', 'coverage']
}

# Giới hạn ký tự cho mỗi file (500,000 ký tự)
CHAR_LIMIT = 500_000

class MetadataExtractor:
    """Class to handle metadata extraction for different file types."""
    
    @staticmethod
    def get_codeblock_language(file_path: Path) -> str:
        """Determine the code block language based on file extension."""
        extension_map = {
            '.sol': 'solidity',
            '.t.sol': 'solidity',
            '.s.sol': 'solidity',
            '.js': 'javascript',
            '.jsx': 'javascript',
            '.ts': 'typescript',
            '.tsx': 'typescript',
            '.rs': 'rust',
            '.css': 'css',
            '.scss': 'scss',
            '.html': 'html',
            '.toml': 'toml',
            '.yml': 'yaml',
            '.yaml': 'yaml',
            '.json': 'json',
            '.mjs': 'javascript',
            '.md': 'markdown',
            '.txt': 'text',
            '.lock': 'text'
        }
        return extension_map.get(file_path.suffix, 'text')

    @staticmethod
    def extract_metadata(content: str, file_ext: str, project_type: str) -> Dict[str, str]:
        """Extract metadata (version and summary) from file content."""
        metadata = {'version': 'Unknown', 'summary': 'No summary comments found.'}

        # Common comment parsing logic
        def parse_comments(lines, comment_starters, max_lines=20):
            summary_lines = []
            for line in lines[:max_lines]:
                line = line.strip()
                if any(line.startswith(starter) for starter in comment_starters):
                    comment = re.sub(r'^({})\s*'.format('|'.join(map(re.escape, comment_starters))), '', line).strip()
                    if comment:
                        summary_lines.append(comment)
                elif line and not any(line.startswith(kw) for kw in ['pragma', 'import', '#', '@']):
                    break
            return ' '.join(summary_lines) if summary_lines else 'No summary comments found.'

        if file_ext in ('.sol', '.t.sol', '.s.sol'):
            # Solidity: Extract version and comments
            pragma_match = re.search(r'pragma\s+solidity\s*([^\s;]+);', content)
            metadata['version'] = pragma_match.group(1) if pragma_match else 'Unknown'
            metadata['summary'] = parse_comments(content.split('\n'), ['//', '/*', '*'])

        elif file_ext in ('.js', '.jsx', '.ts', '.tsx', '.mjs'):
            # JavaScript/TypeScript: Extract version from comments or package.json/hardhat.config
            if project_type == 'hardhat' and 'hardhat.config' in file_ext:
                version_match = re.search(r'solidity:\s*[\'"]([^\'"]+)[\'"]|version:\s*[\'"]([^\'"]+)[\'"]', content)
                metadata['version'] = version_match.group(1) or version_match.group(2) if version_match else 'Unknown'
            else:
                version_match = re.search(r'@version\s+([^\s]+)|version:\s*[\'"]([^\'"]+)[\'"]', content)
                metadata['version'] = version_match.group(1) or version_match.group(2) if version_match else 'Unknown'
            metadata['summary'] = parse_comments(content.split('\n'), ['//', '/*', '*'])

        elif file_ext == '.rs':
            # Rust: Extract crate or version
            version_match = re.search(r'version\s*=\s*"([^"]+)"|#\[\s*macro_use\s*\]\s*extern\s+crate\s+([^\s;]+);', content)
            metadata['version'] = version_match.group(1) or version_match.group(2) if version_match else 'Unknown'
            metadata['summary'] = parse_comments(content.split('\n'), ['//', '///', '//!'])

        elif file_ext in ('.toml', '.yml', '.yaml', '.json', '.lock'):
            # Config files: Extract version if available
            version_match = re.search(r'version\s*=\s*"([^"]+)"|version:\s*[\'"]([^\'"]+)[\'"]', content)
            metadata['version'] = version_match.group(1) or version_match.group(2) if version_match else 'Unknown'
            metadata['summary'] = parse_comments(content.split('\n'), ['#', '//'])

        return metadata

# Phát hiện loại dự án
def detect_project_type(repo_path: Path) -> Optional[str]:
    """Detect project type based on repository structure."""
    if (repo_path / 'foundry.toml').exists():
        return 'foundry'
    elif (repo_path / 'package.json').exists() and (repo_path / 'next.config.js').exists():
        return 'nextjs'
    elif (repo_path / 'Cargo.toml').exists():
        return 'cargo'
    elif (repo_path / 'hardhat.config.js').exists() or (repo_path / 'hardhat.config.ts').exists():
        return 'hardhat'
    return None

project_type = detect_project_type(repo_path)
if not project_type:
    logger.error("Could not detect project type (Foundry, Next.js, Cargo, or Hardhat)")
    sys.exit(1)

# Hàm tạo header cho mỗi file part
def create_file_header(project_type: str, part_number: int, toc: List[str]) -> List[str]:
    """Create header for each file part with table of contents."""
    header = [
        f'# Detailed Code Analysis for {project_type.capitalize()} Repository (Part {part_number})\n\n',
        f'This document contains a portion of the source code of relevant files ({", ".join(PROJECT_TYPES[project_type])}) '
        f'in the {project_type.capitalize()} repository, with metadata and summaries for analysis in NotebookLM.\n\n'
    ]
    if toc:
        header.append('## Table of Contents\n')
        header.extend(toc)
        header.append('\n')
    return header

# Thu thập nội dung các tệp và chia file
files_found = False
part_number = 1
current_content: List[str] = []
current_toc: List[str] = []
current_char_count = 0
output_files = []
file_extensions = set(PROJECT_TYPES[project_type])  # Cache file extensions for faster lookup

for file_path in sorted(repo_path.rglob('*')):
    if file_path.is_dir() or any(ex_dir in file_path.parts for ex_dir in EXCLUDE_DIRS[project_type]):
        continue
    if file_path.suffix not in file_extensions:
        continue
    files_found = True
    rel_path = file_path.relative_to(repo_path)
    file_content = []
    try:
        content = file_path.read_text(encoding='utf-8')
        metadata = MetadataExtractor.extract_metadata(content, file_path.suffix, project_type)
        file_content = [
            f'## File: {file_path.name}\n\n',
            f'**Path**: `{rel_path}`\n\n',
            f'**Version**: {metadata["version"]}\n\n',
            f'**Summary**: {metadata["summary"]}\n\n',
            f'### Source Code\n',
            f'```{MetadataExtractor.get_codeblock_language(file_path)}\n',
            content + '\n',
            '```\n\n'
        ]
        file_char_count = len(''.join(file_content))
        toc_entry = f'- [{file_path.name}](#file-{file_path.name.replace(".", "-")})\n'
        
        # Kiểm tra xem file có thể thêm vào current_content không
        if current_char_count + file_char_count > CHAR_LIMIT:
            # Ghi file hiện tại
            output_file = output_dir / f'detailed_code_analysis_part{part_number}.md'
            output_file.write_text(''.join(create_file_header(project_type, part_number, current_toc) + current_content), encoding='utf-8')
            output_files.append(output_file)
            logger.info(f"Generated file: {output_file.name} in {output_dir}")
            # Bắt đầu file mới
            part_number += 1
            current_content = []
            current_toc = []
            current_char_count = 0
        
        # Thêm nội dung file và cập nhật TOC
        current_content.extend(file_content)
        current_toc.append(toc_entry)
        current_char_count += file_char_count
    except UnicodeDecodeError:
        logger.warning(f"Cannot read {rel_path}: File is binary or not UTF-8 encoded")
        error_content = [
            f'## File: {file_path.name}\n\n',
            f'**Path**: `{rel_path}`\n\n',
            f'**Error**: File is binary or not UTF-8 encoded, cannot read as text.\n\n'
        ]
        error_char_count = len(''.join(error_content))
        toc_entry = f'- [{file_path.name}](#file-{file_path.name.replace(".", "-")})\n'
        if current_char_count + error_char_count > CHAR_LIMIT:
            output_file = output_dir / f'detailed_code_analysis_part{part_number}.md'
            output_file.write_text(''.join(create_file_header(project_type, part_number, current_toc) + current_content), encoding='utf-8')
            output_files.append(output_file)
            logger.info(f"Generated file: {output_file.name} in {output_dir}")
            part_number += 1
            current_content = []
            current_toc = []
            current_char_count = 0
        current_content.extend(error_content)
        current_toc.append(toc_entry)
        current_char_count += error_char_count
    except Exception as e:
        logger.error(f"Failed to read {rel_path}: {str(e)}")
        error_content = [
            f'## File: {file_path.name}\n\n',
            f'**Path**: `{rel_path}`\n\n',
            f'**Error**: Failed to read file: {str(e)}.\n\n'
        ]
        error_char_count = len(''.join(error_content))
        toc_entry = f'- [{file_path.name}](#file-{file_path.name.replace(".", "-")})\n'
        if current_char_count + error_char_count > CHAR_LIMIT:
            output_file = output_dir / f'detailed_code_analysis_part{part_number}.md'
            output_file.write_text(''.join(create_file_header(project_type, part_number, current_toc) + current_content), encoding='utf-8')
            output_files.append(output_file)
            logger.info(f"Generated file: {output_file.name} in {output_dir}")
            part_number += 1
            current_content = []
            current_toc = []
            current_char_count = 0
        current_content.extend(error_content)
        current_toc.append(toc_entry)
        current_char_count += error_char_count

# Ghi file cuối cùng nếu có nội dung
if current_content:
    output_file = output_dir / f'detailed_code_analysis_part{part_number}.md'
    if not files_found:
        current_content.append(
            f'**Note**: No relevant files ({", ".join(PROJECT_TYPES[project_type])}) were found in the repository.\n\n'
        )
    output_file.write_text(''.join(create_file_header(project_type, part_number, current_toc) + current_content), encoding='utf-8')
    output_files.append(output_file)
    logger.info(f"Generated file: {output_file.name} in {output_dir}")

if not files_found:
    logger.warning(f"No relevant files ({', '.join(PROJECT_TYPES[project_type])}) were found in the {project_type.capitalize()} repository.")

logger.info(f"Total files generated: {len(output_files)}")