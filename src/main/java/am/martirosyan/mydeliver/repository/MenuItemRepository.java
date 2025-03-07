package am.martirosyan.mydeliver.repository;

import am.martirosyan.mydeliver.model.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryId(Long categoryId, Pageable pageable);
    List<MenuItem> findByCategoryId(Long categoryId);
}
