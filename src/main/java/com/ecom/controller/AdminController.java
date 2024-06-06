package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Les Beans
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ProductService productService;

    // Les Method

    // -----------------------------
    // index methods_01
    // -----------------------------

    @GetMapping("/")
    public String index() {
        return "admin/index";
    }

    // -----------------------------
    // category methods
    // -----------------------------

    @GetMapping("/category")
    public String category(Model m) {
        m.addAttribute("categorys", categoryService.getAllCategory());
        return "admin/category";

    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {

        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);


        Boolean existCategory = categoryService.existCategory(category.getName());

        if (existCategory) {
            session.setAttribute("errorMsg", "Category Name already exists");
        } else {

            Category saveCategory = categoryService.saveCategory(category);

            if (ObjectUtils.isEmpty(saveCategory)) {
                session.setAttribute("errorMsg", "not saved ! internal server error ");
            } else {

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
                        + file.getOriginalFilename());
                System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                session.setAttribute("succMsg", "saved successfully");
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {
        Boolean deleCategory = categoryService.deleteCategory(id);

        if (deleCategory) {
            session.setAttribute("succMsg", "category deleted with success");
        } else {
            session.setAttribute("errorMsg", "something wrong on server OR he category do no exists");
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model m) {
        m.addAttribute("category", categoryService.getCategoryById(id));

        return "/admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {
        Category oldCategory = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

        if (!ObjectUtils.isEmpty(category)) {
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(imageName);

        }

        Category updateCategory = categoryService.saveCategory(oldCategory);
        if (!ObjectUtils.isEmpty(updateCategory)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
                        + file.getOriginalFilename());
                // System.out.println(path);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            session.setAttribute("succMsg", "Category successfully updated");
        } else {
            session.setAttribute("errorMsg", "update Category rejected");
            // session.setAttribute("errorMsg", "something wrong on server OR he category do
            // no exists");
        }
        return "redirect:loadEditCategory/" + category.getId();
    }

    // -----------------------------
    // product methods part_01
    // -----------------------------

    // NB: ce blok est copié plus bas en vue de lecture pour résoudre un Pb=>
    // supprimer celui d'en bas et réactiver ce block
    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m) {
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        System.out.println(categories);
        return "admin/add_product";
    }

    // relecture ok 20240518
    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
            HttpSession session) throws IOException {
        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product saveProduct = productService.saveProduct(product);
        if (!ObjectUtils.isEmpty(saveProduct)) {
            File saveFile = new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                    + image.getOriginalFilename());
            System.out.println(path);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("succMsg", "Product successfully saved");
        } else {
            session.setAttribute("errorMsg", "somethings wrong on server");
        }
        return "redirect:/admin/loadAddProduct";
    }

    // relecture ok 20240518
    @GetMapping("/products")
    public String loadViewProduct(Model m) {
        m.addAttribute("products", productService.getAllProducts());
        return "/admin/products";
    }

    // relecture ok 20240518
    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
        Boolean deleteProduct = productService.deleteProduct(id);
        if (deleteProduct) {
            session.setAttribute("succMsg", "product deleted with success");
        } else {
            session.setAttribute("errorMsg", "delete product failed");
        }
        return "redirect:/admin/products";
    }

    // relecture ok 20240518
    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, Model m) {
        m.addAttribute("product", productService.getProductById(id));
        m.addAttribute("categories", categoryService.getAllCategory());
        return "/admin/edit_product";
    }

    //  relecture ok 20240518
    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
        HttpSession session, Model m) 
    {
        if(product.getDiscount()<0 || product.getDiscount()>100)
        {
            session.setAttribute("errorMsg", "invalid Discount");
        }
        else
        {
            Product updateProduct = productService.updateProduct(product, image);
            if (!ObjectUtils.isEmpty(updateProduct)) 
            {
                session.setAttribute("succMsg", "product updated with success");
            } else 
            {
                session.setAttribute("errorMsg", "update product failed");
            }
        }
        return "redirect:/admin/editProduct/"+product.getId();
    }

}
