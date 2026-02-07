package Nhom5.ThachTrongKhang;

import Nhom5.ThachTrongKhang.entities.Category;
import Nhom5.ThachTrongKhang.entities.Role;
import Nhom5.ThachTrongKhang.entities.User;
import Nhom5.ThachTrongKhang.repositories.ICategoryRepository;
import Nhom5.ThachTrongKhang.repositories.IRoleRepository;
import Nhom5.ThachTrongKhang.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final ICategoryRepository categoryRepository;
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Khởi tạo Roles
        if (roleRepository.count() == 0) {
            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .description("Administrator role")
                    .build();
            
            Role userRole = Role.builder()
                    .name("USER")
                    .description("User role")
                    .build();
            
            adminRole = roleRepository.save(adminRole);
            userRole = roleRepository.save(userRole);
            System.out.println("Đã khởi tạo dữ liệu Role thành công!");
        }
        
        // Khởi tạo tài khoản Admin mẫu
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(new BCryptPasswordEncoder().encode("admin123"))
                    .email("admin@example.com")
                    .phone("0123456789")
                    .provider("LOCAL")
                    .build();
            
            Set<Role> adminRoles = new HashSet<>();
            // Lấy role ADMIN từ database
            Role adminRole = roleRepository.findAll().stream()
                    .filter(r -> "ADMIN".equals(r.getName()))
                    .findFirst()
                    .orElseThrow();
            adminRoles.add(adminRole);
            admin.setRoles(adminRoles);
            
            userRepository.save(admin);
            System.out.println("Đã tạo tài khoản Admin mẫu - Username: admin, Password: admin123");
        }
        
        // Khởi tạo tài khoản User mẫu
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = User.builder()
                    .username("user")
                    .password(new BCryptPasswordEncoder().encode("user123"))
                    .email("user@example.com")
                    .phone("0987654321")
                    .provider("LOCAL")
                    .build();
            
            Set<Role> userRoles = new HashSet<>();
            // Lấy role USER từ database
            Role userRole = roleRepository.findAll().stream()
                    .filter(r -> "USER".equals(r.getName()))
                    .findFirst()
                    .orElseThrow();
            userRoles.add(userRole);
            user.setRoles(userRoles);
            
            userRepository.save(user);
            System.out.println("Đã tạo tài khoản User mẫu - Username: user, Password: user123");
        }
        
        // Chỉ khởi tạo categories nếu chưa có
        if (categoryRepository.count() == 0) {
            // Tạo các môn học
            Category congNghePhanMem = Category.builder()
                    .name("Công nghệ phần mềm")
                    .build();
            
            Category coBanLapTrinh = Category.builder()
                    .name("Cơ sở lập trình")
                    .build();
            
            Category coBanDuLieu = Category.builder()
                    .name("Cơ sở dữ liệu")
                    .build();
            
            Category mangMayTinh = Category.builder()
                    .name("Mạng máy tính")
                    .build();
            
            Category heDieuHanh = Category.builder()
                    .name("Hệ điều hành")
                    .build();
            
            Category trinhTuThuat = Category.builder()
                    .name("Cấu trúc dữ liệu và giải thuật")
                    .build();
            
            Category lapTrinhWeb = Category.builder()
                    .name("Lập trình Web")
                    .build();
            
            Category anNinhMang = Category.builder()
                    .name("An ninh mạng")
                    .build();
            
            // Lưu vào database
            categoryRepository.save(congNghePhanMem);
            categoryRepository.save(coBanLapTrinh);
            categoryRepository.save(coBanDuLieu);
            categoryRepository.save(mangMayTinh);
            categoryRepository.save(heDieuHanh);
            categoryRepository.save(trinhTuThuat);
            categoryRepository.save(lapTrinhWeb);
            categoryRepository.save(anNinhMang);
            
            System.out.println("Đã khởi tạo dữ liệu Category thành công!");
        }
    }
}
