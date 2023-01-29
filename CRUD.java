import java.lang.Exception;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class CRUD {

	static Connection koneksi = null;

	static Scanner scannerInt = new Scanner(System.in);
	static Scanner scannerStr = new Scanner(System.in);
	static NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));

	public static void main(String[] args) {
		buatKoneksi();
		buatTabel();
		System.out.println("-----------------------------------------");

		Integer pilihan = 0;
		while (true) {
			System.out.println("Aplikasi CRUD Menu Restoran");
			System.out.println("1. Lihat Daftar Menu");
			System.out.println("2. Buat Menu Baru");
			System.out.println("3. Ubah Menu");
			System.out.println("4. Hapus Menu");
			System.out.println("5. Keluar");
			System.out.print("Silakan pilih operasi (1-5) : ");
			try {
				pilihan = scannerInt.nextInt();
			} catch (Exception e) {
				scannerInt.nextLine();
				System.out.println("");
				System.out.println("Silakan pilih operasi berupa angka dari 1 sampai 5!");
				System.out.println("-----------------------------------------");
				continue;
			}
			System.out.println("-----------------------------------------");
			switch (pilihan) {
			case 1:
				lihatMenu();
				break;
			case 2:
				tambahMenu();
				break;
			case 3:
				ubahMenu();
				break;
			case 4:
				hapusMenu();
				break;
			case 5:
				System.out.println("Terima Kasih");
				System.exit(0);
				break;
			default:
				System.out.println("Operasi Tidak Valid!");
				break;
			}
			System.out.println("-----------------------------------------");
		}
	}

	public static void buatKoneksi() {
		try {
			koneksi = DriverManager.getConnection("jdbc:sqlite:crud.db");
			System.out.println("Berhasil terhubung ke database crud.db...");
		} catch (Exception e) {
			System.out.println("Gagal terhubung ke database crud.db : " + e.getMessage());
			System.exit(1);
		}
	}

	public static void buatTabel() {
		String sql = ""
			+ "CREATE TABLE IF NOT EXISTS menu_restoran ("
			+ "  id INTEGER,"
			+ "  nomor INTEGER,"
			+ "  nama TEXT,"
			+ "  harga INTEGER,"
			+ "  PRIMARY KEY (id AUTOINCREMENT)"
			+ "  UNIQUE (nomor)"
			+ ");";
		try {
			Statement stmt = koneksi.createStatement();
			stmt.execute(sql);
			System.out.println("Tabel menu_restoran berhasil dibuat...");
		} catch (Exception e) {
			System.out.println("Gagal membuat tabel menu_restoran : " + e.getMessage());
			System.exit(1);
		}
	}

	public static void lihatMenu() {
		System.out.println("1. Lihat Daftar Menu");
		String sql = "SELECT nomor, nama, harga FROM menu_restoran ORDER BY nomor";
		try (
			Statement stmt = koneksi.createStatement();
			ResultSet rs = stmt.executeQuery(sql)) {
			ArrayList<MenuResto> daftarMenu = new ArrayList<MenuResto>();
			while (rs.next()) {
				MenuResto m = new MenuResto();
				m.nomor = rs.getInt("nomor");
				m.nama = rs.getString("nama");
				m.harga = rs.getInt("harga");
				daftarMenu.add(m);
			}
			cetakDaftarMenu(daftarMenu);
		} catch (Exception e) {
			System.out.println("");
			System.out.println("Gagal membuat tabel menu_restoran : " + e.getMessage());
		}
	}

	public static void tambahMenu() {
		System.out.println("2. Buat Menu Baru");

		Integer nomor = 0;
		Integer harga;
		String nama;
		String sql = "INSERT INTO menu_restoran (nomor, nama, harga) VALUES (?,?,?)";
		try {
			System.out.print("Masukan Nomor Menu : "); nomor = scannerInt.nextInt();
			System.out.print("Masukan Nama Menu  : "); nama = scannerStr.nextLine();
			System.out.print("Masukan Harga Menu : "); harga = scannerInt.nextInt();

			PreparedStatement ps = koneksi.prepareStatement(sql);
			ps.setInt(1, nomor);
			ps.setString(2, nama);
			ps.setInt(3, harga);
			ps.executeUpdate();
			System.out.println("");
			System.out.println("Menu " + nama + " berhasil ditambahkan!");
		} catch (InputMismatchException e) {
			scannerInt.nextLine();
			System.out.println("");
			System.out.println("Angka tidak valid!");
		} catch (Exception e) {
			System.out.println("");
			if (e.getMessage().contains("UNIQUE constraint failed: menu_restoran.nomor")) {
				System.out.println("Menu dengan nomor " + nomor + " sudah ada!");
			} else {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void ubahMenu() {
		System.out.println("3. Ubah Menu");

		Integer nomor = 0;
		Integer nomorBaru, harga;
		String nama;
		String sql = "UPDATE menu_restoran SET nomor = ?, nama = ?, harga = ? WHERE id = ?";
		try {
			System.out.print("Masukan Nomor Menu Yang Akan Diubah : "); nomor = scannerInt.nextInt();

			MenuResto m = getMenuByNomor(nomor);
			if (m.id == 0) {
				System.out.println("");
				System.out.println("Menu dengan nomor " + nomor + " tidak ditemukan!");
				return;
			}

			System.out.print("Masukan Nomor Menu : "); nomorBaru = scannerInt.nextInt();
			System.out.print("Masukan Nama Menu  : "); nama = scannerStr.nextLine();
			System.out.print("Masukan Harga Menu : "); harga = scannerInt.nextInt();

			PreparedStatement ps = koneksi.prepareStatement(sql);
			ps.setInt(1, nomorBaru);
			ps.setString(2, nama);
			ps.setInt(3, harga);
			ps.setInt(4, m.id);
			ps.executeUpdate();
			System.out.println("");
			System.out.println("Menu dengan nomor " + nomor + " berhasil diubah!");
		} catch (InputMismatchException e) {
			scannerInt.nextLine();
			System.out.println("");
			System.out.println("Angka tidak valid!");
		} catch (Exception e) {
			System.out.println("");
			if (e.getMessage().contains("UNIQUE constraint failed: menu_restoran.nomor")) {
				System.out.println("Menu dengan nomor " + nomor + " sudah ada!");
			} else {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void hapusMenu() {
		System.out.println("4. Hapus Menu");

		Integer nomor;
		String sql = "DELETE FROM menu_restoran WHERE id = ?";
		try {
			System.out.print("Masukan Nomor Menu Yang Akan Dihapus : "); nomor = scannerInt.nextInt();

			MenuResto m = getMenuByNomor(nomor);
			if (m.id == 0) {
				System.out.println("");
				System.out.println("Menu dengan nomor " + nomor + " tidak ditemukan!");
				return;
			}

			PreparedStatement ps = koneksi.prepareStatement(sql);
			ps.setInt(1, m.id);
			ps.executeUpdate();
			System.out.println("");
			System.out.println("Menu dengan nomor " + nomor + " berhasil dihapus!");
		} catch (InputMismatchException e) {
			scannerInt.nextLine();
			System.out.println("");
			System.out.println("Angka tidak valid!");
		} catch (Exception e) {
			System.out.println("");
			System.out.println(e.getMessage());
		}
	}

	public static MenuResto getMenuByNomor(Integer nomor) {
		MenuResto m = new MenuResto();
		m.id = 0;
		String sql = "SELECT id, nomor, nama, harga FROM menu_restoran WHERE nomor = ? LIMIT 1";
		try {
			PreparedStatement ps = koneksi.prepareStatement(sql);
			ps.setInt(1, nomor);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				m.id = rs.getInt("id");
				m.nomor = rs.getInt("nomor");
				m.nama = rs.getString("nama");
				m.harga = rs.getInt("harga");
			}
		} catch (Exception e) {}

		return m;
	}

	public static String cetakDaftarMenu(ArrayList<MenuResto> daftarMenu) {
		String garisBatas = "";
		String labelNomor = "No.";
		String labelNama = "Nama Menu";
		String labelHarga = "Harga";

		Integer nomorLength = labelNomor.length();
		Integer namaLength = labelNama.length();
		Integer hargaLength = labelHarga.length();
		Integer garisLength = 10;

		for (int i = 0; i < daftarMenu.size(); i++) {
			if (nf.format(daftarMenu.get(i).nomor).length() > nomorLength) {
				nomorLength = nf.format(daftarMenu.get(i).nomor).length();
			}
			if (daftarMenu.get(i).nama.length() > namaLength) {
				namaLength = daftarMenu.get(i).nama.length();
			}
			if (nf.format(daftarMenu.get(i).harga).length() > hargaLength) {
				hargaLength = nf.format(daftarMenu.get(i).harga).length();
			}
		}
		Integer maxLength = nomorLength + namaLength + hargaLength + garisLength;
		for (int i = 0; i < maxLength; i++) {
			garisBatas += "-";
		}

		// cetak header
		System.out.println(garisBatas);
		System.out.print("| ");
		for (int i = 0; i < (nomorLength-labelNomor.length()); i++) {
			System.out.print(" ");
		}
		System.out.print(labelNomor + " | " + labelNama);
		for (int i = 0; i < (namaLength-labelNama.length()); i++) {
			System.out.print(" ");
		}
		System.out.print(" | ");
		for (int i = 0; i < (hargaLength-labelHarga.length()); i++) {
			System.out.print(" ");
		}
		System.out.println(labelHarga + " |");
		System.out.println(garisBatas);

		// cetak body
		for (int idx = 0; idx < daftarMenu.size(); idx++) {
			labelNomor = nf.format(daftarMenu.get(idx).nomor);
			labelNama = daftarMenu.get(idx).nama;
			labelHarga = nf.format(daftarMenu.get(idx).harga);
			System.out.print("| ");
			for (int i = 0; i < (nomorLength-labelNomor.length()); i++) {
				System.out.print(" ");
			}
			System.out.print(labelNomor + " | " + labelNama);
			for (int i = 0; i < (namaLength-labelNama.length()); i++) {
				System.out.print(" ");
			}
			System.out.print(" | ");
			for (int i = 0; i < (hargaLength-labelHarga.length()); i++) {
				System.out.print(" ");
			}
			System.out.println(labelHarga + " |");
		}
		System.out.println(garisBatas);
		return garisBatas;
	}
}

class MenuResto {
	public Integer id, nomor, harga;
	public String nama;
}