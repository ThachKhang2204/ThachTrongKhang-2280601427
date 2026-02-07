package Nhom5.ThachTrongKhang.repositories;

import Nhom5.ThachTrongKhang.entities.ItemInvoice;
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository; 
 
@Repository 
public interface IItemInvoiceRepository extends 
JpaRepository<ItemInvoice, Long>{ 
}
