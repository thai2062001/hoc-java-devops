import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Employee> employeeList = new ArrayList<>();

        employeeList.add(new FullTimeEmployee(1, "Nguyen Van Huy", 10000000, 2000000));
        employeeList.add(new PartTimeEmployee(2, "Tran Thi An", 0, 20, 300000));
        employeeList.add(new FullTimeEmployee(3, "Le Van Binh", 15000000, 3000000));

        // 1. In danh sách dùng Lambda (Thay cho vòng lặp for truyền thống)
        System.out.println("=== DANH SÁCH NHÂN VIÊN ===");
        employeeList.forEach(emp -> 
            System.out.println(emp.getName() + " - Lương: " + emp.calculateSalary())
        );

        // 2. Dùng STREAM API để tính TỔNG LƯƠNG công ty phải trả (Chỉ tốn 1 dòng code)
        double totalSalary = employeeList.stream()
                .mapToDouble(Employee::calculateSalary) // Lấy ra lương từng người
                .sum();                                 // Cộng tổng lại
        System.out.println("\n-> Tổng quỹ lương công ty: " + totalSalary + " VND");

        // 3. Dùng STREAM API để LỌC ra những người có lương > 10 triệu
        System.out.println("\n=== NHÂN VIÊN LƯƠNG TRÊN 10 TRIỆU ===");
        employeeList.stream()
                .filter(emp -> emp.calculateSalary() > 10000000) // Bộ lọc
                .forEach(emp -> System.out.println(emp.getName()));

        System.out.println("\n=== TÊN NHÂN VIÊN LÀ FULLTIMEEMPLOYEE ===");
        employeeList.stream()
                .filter(emp -> emp instanceof FullTimeEmployee) // Bộ lọc
                .forEach(emp -> System.out.println(emp.getName()));

      System.out.println("\n=== TÊN NHÂN VIÊN LÀ FULLTIMEEMPLOYEE ===");
        employeeList.stream()
                .filter(emp -> emp.calculateSalary() > 5000000) // Bộ lọc
                .forEach(emp -> System.out.println(emp.getName()));

        System.out.println("\n=== TÊN NHÂN VIÊN LÀ an or huy ===");
        List<Employee> filteredList = employeeList.stream()
                .filter(emp -> emp.getName().toLowerCase().contains("an") || emp.getName().toLowerCase().contains("huy"))
                .toList(); // Gom thành List trước

        filteredList.forEach(emp -> System.out.println(emp.getName())); // Rồi mới in
    }
}