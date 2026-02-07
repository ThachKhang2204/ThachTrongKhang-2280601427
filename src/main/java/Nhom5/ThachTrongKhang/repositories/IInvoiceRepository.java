package Nhom5.ThachTrongKhang.repositories;

import Nhom5.ThachTrongKhang.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository; 
@Repository 
public interface IInvoiceRepository extends JpaRepository<Invoice, 
Long>{ 
} 
