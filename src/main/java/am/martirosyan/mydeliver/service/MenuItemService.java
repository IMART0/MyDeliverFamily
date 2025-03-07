package am.martirosyan.mydeliver.service;

import am.martirosyan.mydeliver.model.MenuItem;
import am.martirosyan.mydeliver.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;

    public List<MenuItem> getByCategoryId(Long categoryId, int page) {
        Pageable pageable = PageRequest.of(page, 4);
        return menuItemRepository.findByCategoryId(categoryId, pageable);
    }

    public List<MenuItem> getByCategoryId(Long categoryId) {
        return menuItemRepository.findByCategoryId(categoryId);
    }

}
