package com.example.jpa.controller;

import com.example.jpa.entity.Cart;
import com.example.jpa.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/cart/list")
    public String viewCart(@RequestParam("customerId") Long customerId, Model model) {
        // Retrieve cart items for the given customer ID
        List<Cart> cartItems = cartService.getCartItemsByCustomerId(customerId); // 고객이 가지고 있는 장바구니 목록을 가져옴
        double totalAmount = cartService.getTotalAmount(cartItems); //장바구니에 담긴 총 구매량

        model.addAttribute("cartItems", cartItems);//장바구니 목록
        model.addAttribute("totalAmount", totalAmount); //가격

        return "cartList"; // Return the cart list view
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("bookId") Long bookId, @RequestParam("customerId") Long customerId) {
        cartService.addToCart(bookId, customerId);
        return "redirect:/books"; // Redirect to the book list page after adding to cart
    }

    @GetMapping("/cart/delete/{id}")
    public String cartDelete(@PathVariable Long id){
        Cart cart=cartService.findById(id)//카트 정보를 가져옴
                .orElseThrow(() -> new RuntimeException("Cart not found")); ;
        cartService.cartDelete(cart); //해당되는 카트를 삭제
        // cart.getCustomer().getId()
        //return "redirect:/books";
        return "redirect:/cart/list?customerId="+cart.getCustomer().getId();
    }

    @GetMapping("/cart/edit/{id}/{quantity}")
    public String quantityUpdate(@PathVariable Long id, @PathVariable int quantity){
        Cart cart=cartService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found")); ;
        cart.setQuantity(quantity);
        cartService.suUpdate(cart); // 수정
        return "redirect:/cart/list?customerId="+cart.getCustomer().getId();
    }
}