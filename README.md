# 💰 MoneyMind - Personal Finance Management App

## 📱 Giới thiệu
**MoneyMind** là ứng dụng quản lý tài chính cá nhân trên nền tảng Android, giúp người dùng theo dõi thu chi, lập kế hoạch ngân sách và đạt được mục tiêu tài chính.

### ✨ Tính năng chính

#### 🏠 Dashboard (Trang chủ)
- Hiển thị tổng quan chi tiêu trong tháng
- Theo dõi tiến độ mục tiêu tài chính
- Danh sách 5 giao dịch gần nhất
- Biểu đồ chi tiêu theo thời gian

#### 💳 Quản lý Giao dịch (Transactions)
- Thêm/Sửa/Xóa giao dịch
- Phân loại: Thu nhập (Income) / Chi tiêu (Expense)
- Ghi chú chi tiết cho từng giao dịch
- Sắp xếp theo số tiền, ngày tháng
- Swipe để chỉnh sửa hoặc xóa nhanh

#### 👛 Quản lý Ví (Wallets)
- Tạo nhiều ví khác nhau (Tiền mặt, Ngân hàng, v.v.)
- Theo dõi số dư từng ví
- Tự động cập nhật số dư khi thêm giao dịch
- Phân loại ví theo danh mục

#### 🎯 Mục tiêu hàng tháng (Monthly Goals)
- Đặt mục tiêu chi tiêu cho từng tháng
- Theo dõi tiến độ qua biểu đồ
- Hỗ trợ 6 Jars Money Management System:
  - **NEC** (Necessities - 55%): Chi tiêu cần thiết
  - **LTS** (Long-term Savings - 10%): Tiết kiệm dài hạn
  - **EDU** (Education - 10%): Đầu tư giáo dục
  - **PLY** (Play - 10%): Hưởng thụ
  - **FFA** (Financial Freedom - 10%): Tự do tài chính
  - **GIV** (Give - 5%): Cho đi

#### 🎨 Theme & UI/UX
- **Dark Mode / Light Mode** với chuyển đổi mượt mà
- Chế độ theo hệ thống (System Mode)
- Animation hiện đại
- Giao diện Material Design
- Bottom Navigation để điều hướng dễ dàng

#### 📊 Xuất dữ liệu
- Export giao dịch ra file CSV
- Chọn khoảng thời gian xuất dữ liệu
- Import dữ liệu từ CSV

---

## 🏗️ Kiến trúc dự án

### Technology Stack
- **Language**: Java
- **Platform**: Android (API 24+)
- **Database**: SQLite
- **UI Framework**: Material Design Components
- **Chart Library**: MPAndroidChart
- **CSV Library**: OpenCSV

### Project Structure
```
app/
├── src/main/
│   ├── java/com/prm/money/
│   │   ├── data/
│   │   │   ├── db/
│   │   │   │   └── MoneyMindDbHelper.java       # Database helper
│   │   │   └── repository/
│   │   │       ├── TransactionDao.java          # Transaction data access
│   │   │       ├── TransactionService.java      # Business logic
│   │   │       ├── WalletDao.java               # Wallet data access
│   │   │       ├── WalletCategoryDao.java       # Category data access
│   │   │       └── MonthlyGoalDao.java          # Goal data access
│   │   │
│   │   ├── model/
│   │   │   ├── Transaction.java                 # Transaction entity
│   │   │   ├── Wallet.java                      # Wallet entity
│   │   │   ├── WalletCategory.java              # Category entity
│   │   │   └── MonthlyGoal.java                 # Goal entity
│   │   │
│   │   ├── ui/
│   │   │   ├── activity/
│   │   │   │   ├── MainActivity.java            # Main activity
│   │   │   │   ├── AddEditTransactionActivity.java
│   │   │   │   ├── AddEditWalletActivity.java
│   │   │   │   ├── AddEditGoalActivity.java
│   │   │   │   └── ThemeSettingsActivity.java
│   │   │   │
│   │   │   ├── fragment/
│   │   │   │   ├── HomeFragment.java            # Dashboard
│   │   │   │   ├── TransactionListFragment.java # Transaction list
│   │   │   │   ├── WalletListFragment.java      # Wallet list
│   │   │   │   └── GoalListFragment.java        # Goal list
│   │   │   │
│   │   │   └── adapter/
│   │   │       ├── TransactionAdapter.java      # RecyclerView adapter
│   │   │       ├── WalletAdapter.java
│   │   │       ├── GoalAdapter.java
│   │   │       └── SwipeHelper.java             # Swipe gesture
│   │   │
│   │   ├── utils/
│   │   │   └── ThemeManager.java                # Theme management
│   │   │
│   │   ├── csv/
│   │   │   └── CsvExporter.java                 # CSV export/import
│   │   │
│   │   └── MoneyMindApplication.java            # Application class
│   │
│   └── res/
│       ├── layout/                               # XML layouts
│       ├── drawable/                             # Icons & images
│       ├── values/                               # Colors, strings, themes (Light)
│       ├── values-night/                         # Dark mode colors & themes
│       └── anim/                                 # Animations
```

### Database Schema

#### Transaction Table
```sql
CREATE TABLE Transaction (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amount REAL NOT NULL,
    date TEXT NOT NULL,
    note TEXT,
    walletId INTEGER NOT NULL,
    categoryId INTEGER NOT NULL,
    type TEXT NOT NULL,              -- 'income' or 'expense'
    activity TEXT,
    monthlyGoalId INTEGER,
    createdAt TEXT,
    updatedAt TEXT,
    isDelete INTEGER DEFAULT 0,
    FOREIGN KEY(walletId) REFERENCES Wallet(id),
    FOREIGN KEY(categoryId) REFERENCES WalletCategory(id),
    FOREIGN KEY(monthlyGoalId) REFERENCES MonthlyGoal(id)
);
```

#### Wallet Table
```sql
CREATE TABLE Wallet (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    balance REAL NOT NULL,
    description TEXT,
    categoryId INTEGER,
    icon TEXT,
    isDelete INTEGER DEFAULT 0,
    FOREIGN KEY(categoryId) REFERENCES WalletCategory(id)
);
```

#### MonthlyGoal Table
```sql
CREATE TABLE MonthlyGoal (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    month INTEGER NOT NULL,
    year INTEGER NOT NULL,
    goalAmount REAL NOT NULL,
    categoryId INTEGER NOT NULL,
    walletId INTEGER,
    note TEXT,
    createdAt TEXT,
    updatedAt TEXT,
    isDelete INTEGER DEFAULT 0,
    FOREIGN KEY(categoryId) REFERENCES WalletCategory(id),
    FOREIGN KEY(walletId) REFERENCES Wallet(id),
    UNIQUE(categoryId, month, year)
);
```

#### WalletCategory Table
```sql
CREATE TABLE WalletCategory (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    color TEXT,
    icon TEXT,
    isDelete INTEGER DEFAULT 0
);
```

---

## 🚀 Hướng dẫn cài đặt

### Yêu cầu hệ thống
- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 11 trở lên
- Android SDK API 24 (Android 7.0) trở lên
- Gradle 8.0+

### Các bước cài đặt

1. **Clone repository**
```bash
git clone https://github.com/sangdnSE184169/PRM392_Project.git
cd PRM392_Project
```

2. **Mở project trong Android Studio**
   - Chọn `File` → `Open`
   - Chọn thư mục project vừa clone

3. **Sync Gradle**
   - Android Studio sẽ tự động sync Gradle
   - Hoặc click `File` → `Sync Project with Gradle Files`

4. **Chạy ứng dụng**
   - Kết nối thiết bị Android hoặc khởi động emulator
   - Click nút `Run` (▶️) hoặc nhấn `Shift + F10`

### Dependencies chính
```gradle
dependencies {
    // Material Design
    implementation 'com.google.android.material:material:1.9.0'
    
    // MPAndroidChart for charts
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
    
    // OpenCSV for CSV export/import
    implementation 'com.opencsv:opencsv:5.5.2'
    
    // AppCompat
    implementation 'androidx.appcompat:appcompat:1.6.1'
    
    // ConstraintLayout
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
}
```

---

## 📖 Hướng dẫn sử dụng

### 1. Tạo ví mới
1. Vào tab **Wallets** (👛)
2. Nhấn nút **+** ở góc dưới
3. Nhập thông tin: Tên ví, Số dư ban đầu, Mô tả
4. Chọn danh mục (Category)
5. Nhấn **Save**

### 2. Thêm giao dịch
1. Vào tab **Transactions** (📋)
2. Nhấn nút **+** ở góc dưới
3. Chọn loại: Thu nhập hoặc Chi tiêu
4. Nhập số tiền, chọn ví, danh mục
5. Thêm ghi chú (tùy chọn)
6. Chọn ngày giao dịch
7. Nhấn **Save**

### 3. Đặt mục tiêu tháng
1. Vào tab **Goals** (🎯)
2. Nhấn nút **+** ở góc dưới
3. Chọn tháng/năm
4. Nhập số tiền mục tiêu
5. Chọn danh mục (6 Jars)
6. Nhấn **Save**

### 4. Chuyển đổi Dark Mode
- **Cách 1**: Nhấn nút 🌙/☀️ ở góc trên phải
- **Cách 2**: Menu (⋮) → Cài đặt giao diện → Chọn theme

### 5. Xuất dữ liệu
1. Mở menu (⋮) ở góc trên phải
2. Chọn **Export to CSV**
3. Chọn khoảng thời gian
4. File CSV sẽ được lưu vào Downloads

---

## 📝 Tài liệu bổ sung
- [Dark Mode Guide](DARK_MODE_GUIDE.md) - Hướng dẫn chi tiết về Dark Mode

---

## 🐛 Bug Reports & Feature Requests
Nếu bạn phát hiện lỗi hoặc có ý tưởng tính năng mới, vui lòng tạo [Issue](https://github.com/sangdnSE184169/PRM392_Project/issues) trên GitHub.

---

## 📄 License
This project is developed for educational purposes as part of PRM392 course.

---

## 🙏 Acknowledgments
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - For beautiful charts
- [OpenCSV](http://opencsv.sourceforge.net/) - For CSV handling
- [Material Design](https://material.io/) - For UI components
- 6 Jars Money Management System by T. Harv Eker

---

**Developed with ❤️ by MoneyMind Team**
