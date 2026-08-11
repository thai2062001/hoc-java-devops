package com.example.demodevops.repository;

import com.example.demodevops.model.EmployeeShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeShiftRepository extends JpaRepository<EmployeeShift, Long> {

    List<EmployeeShift> findByWorkDate(LocalDate workDate);

    List<EmployeeShift> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    Optional<EmployeeShift> findByEmployeeIdAndShiftIdAndWorkDate(Long employeeId, Long shiftId, LocalDate workDate);

    // Tìm kiếm kỹ thuật viên có ca làm việc vào ngày chỉ định và trạng thái ca trực hợp lệ (ASSIGNED hoặc CHECKED_IN)
    @Query("SELECT es FROM EmployeeShift es JOIN es.employee e JOIN e.role r " +
           "WHERE es.workDate = :workDate AND r.code = 'TECHNICIAN' " +
           "AND es.status IN (com.example.demodevops.model.EmployeeShift$ShiftStatus.ASSIGNED, com.example.demodevops.model.EmployeeShift$ShiftStatus.CHECKED_IN)")
    List<EmployeeShift> findAvailableTechniciansByDate(@Param("workDate") LocalDate workDate);
}
