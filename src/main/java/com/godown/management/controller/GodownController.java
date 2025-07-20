package com.godown.management.controller;

import com.godown.management.model.Block;
import com.godown.management.model.Miller;
import com.godown.management.model.Truck;
import com.godown.management.service.BlockService;
import com.godown.management.service.MillerService;
import com.godown.management.service.TruckService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class GodownController {

    @Autowired
    private MillerService millerService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private TruckService truckService;
    
    @PostConstruct
    public void init() {
        // Initialize blocks when application starts
        blockService.initializeBlocks();
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/check-miller")
    public String checkMillerForm(Model model) {
        model.addAttribute("miller", new Miller());
        return "check-miller";
    }

    @PostMapping("/check-miller")
    public String checkMiller(@RequestParam("millerCode") String millerCode, Model model, RedirectAttributes redirectAttributes) {
        Miller miller = millerService.findByMillerCode(millerCode);
        
        if (miller != null) {
            // Existing miller - redirect to truck entry form
            redirectAttributes.addFlashAttribute("millerCode", millerCode);
            return "redirect:/add-truck";
        } else {
            // New miller - go to registration page
            model.addAttribute("millerCode", millerCode);
            model.addAttribute("miller", new Miller());
            return "register-miller";
        }
    }

    @GetMapping("/register-miller")
    public String registerMillerForm(Model model) {
        model.addAttribute("miller", new Miller());
        return "register-miller";
    }

    @PostMapping("/register-miller")
    public String registerMiller(@ModelAttribute Miller miller, Model model) {
        // Show available blocks based on capacity
        List<Block> availableBlocks = blockService.findAvailableBlocksByCapacity(miller.getBlockCapacity());
        model.addAttribute("miller", miller);
        model.addAttribute("availableBlocks", availableBlocks);
        return "select-block";
    }

    @PostMapping("/assign-block")
    public String assignBlock(@RequestParam("millerCode") String millerCode, 
                             @RequestParam("millerName") String millerName,
                             @RequestParam("blockCapacity") int blockCapacity,
                             @RequestParam("blockId") Long blockId,
                             RedirectAttributes redirectAttributes) {
        
        // Create and save the miller
        Miller miller = new Miller();
        miller.setMillerCode(millerCode);
        miller.setMillerName(millerName);
        miller.setBlockCapacity(blockCapacity);
        
        // Assign block to miller
        Block block = blockService.assignBlockToMiller(blockId);
        miller.setAssignedBlock(block);
        
        // Save miller
        millerService.saveMiller(miller);
        
        redirectAttributes.addFlashAttribute("message", "Miller registered successfully and assigned to " + 
                                         "Compartment " + block.getCompartmentNumber() + ", Block " + block.getBlockName());
        redirectAttributes.addFlashAttribute("millerCode", millerCode);
        
        return "redirect:/add-truck";
    }

    @GetMapping("/add-truck")
    public String addTruckForm(Model model, @ModelAttribute("millerCode") String millerCode) {
        Truck truck = new Truck();
        
        if (millerCode != null && !millerCode.isEmpty()) {
            Miller miller = millerService.findByMillerCode(millerCode);
            truck.setMiller(miller);
            model.addAttribute("miller", miller);
        }
        
        model.addAttribute("truck", truck);
        return "add-truck";
    }
    
    @PostMapping("/add-truck")
    public String addTruck(@ModelAttribute Truck truck, RedirectAttributes redirectAttributes) {
        // Calculate charges
        truck.setCharges(15 * truck.getGunnyBags());
        
        // Default arrival date to today if not provided
        if (truck.getArrivalDate() == null) {
            truck.setArrivalDate(LocalDate.now());
        }
        
        // Default unloading date to today if not provided
        if (truck.getUnloadingDate() == null) {
            truck.setUnloadingDate(LocalDate.now());
        }
        
        // --- BEGIN MODIFICATION ---
        // Get the Miller from the truck object (populated by form binding)
        Miller millerFromForm = truck.getMiller();
        Miller managedMiller = null;

        if (millerFromForm != null && millerFromForm.getMillerCode() != null && !millerFromForm.getMillerCode().isEmpty()) {
            // Fetch the complete Miller object from the database
            managedMiller = millerService.findByMillerCode(millerFromForm.getMillerCode());
            
            if (managedMiller == null) {
                // Miller not found in DB, handle error (e.g., redirect with an error message)
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Miller with code " + millerFromForm.getMillerCode() + " not found.");
                // Redirect back to the form, or an error page. Make sure millerCode is available if redirecting to add-truck form.
                if (millerFromForm.getMillerCode() != null) {
                    redirectAttributes.addFlashAttribute("millerCode", millerFromForm.getMillerCode());
                }
                return "redirect:/add-truck"; 
            }
            // Set the fully managed Miller object on the truck
            truck.setMiller(managedMiller);
        } else {
            // Miller information (e.g., millerCode) was not provided with the truck form, handle error
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Miller information is missing for the truck.");
             // Potentially redirect to check-miller if no millerCode context is available
            return "redirect:/check-miller";
        }
        // --- END MODIFICATION ---
        
        // Save truck (now truck.miller is the fully loaded miller)
        truckService.saveTruck(truck);
        
        // Get miller and block information for confirmation
        // 'managedMiller' is already the correct, fully loaded Miller object.
        Block block = managedMiller.getAssignedBlock(); 
        
        // Add a null check for 'block' before using it, even after loading the managedMiller.
        // This handles cases where a Miller might exist but genuinely has no block assigned in the DB.
        if (block == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: Miller " + managedMiller.getMillerName() + 
                                                 " (Code: " + managedMiller.getMillerCode() + ") does not have an assigned block. " +
                                                 "Please ensure a block is assigned to this miller.");
            // Redirect to a page where block can be assigned or to an info page.
            // If you redirect to add-truck, pass the millerCode again.
            redirectAttributes.addFlashAttribute("millerCode", managedMiller.getMillerCode());
            return "redirect:/add-truck"; 
        }
        
        redirectAttributes.addFlashAttribute("message", "Truck added successfully!");
        redirectAttributes.addFlashAttribute("truckDetails", "Truck: " + truck.getTruckName() + 
                                              " for Miller: " + managedMiller.getMillerName() + 
                                              " - Please go to Compartment " + block.getCompartmentNumber() + 
                                              ", Block " + block.getBlockName());
        
        return "redirect:/success";
    }

//    @PostMapping("/add-truck")
//    public String addTruck(@ModelAttribute Truck truck, RedirectAttributes redirectAttributes) {
//        // Calculate charges
//        truck.setCharges(15 * truck.getGunnyBags());
//        
//        // Default arrival date to today if not provided
//        if (truck.getArrivalDate() == null) {
//            truck.setArrivalDate(LocalDate.now());
//        }
//        
//        // Default unloading date to today if not provided
//        if (truck.getUnloadingDate() == null) {
//            truck.setUnloadingDate(LocalDate.now());
//        }
//        
//        // Save truck
//        truckService.saveTruck(truck);
//        
//        // Get miller and block information for confirmation
//        Miller miller = truck.getMiller();
//        Block block = miller.getAssignedBlock();
//        
//        redirectAttributes.addFlashAttribute("message", "Truck added successfully!");
//        redirectAttributes.addFlashAttribute("truckDetails", "Truck: " + truck.getTruckName() + 
//                                        " for Miller: " + miller.getMillerName() + 
//                                        " - Please go to Compartment " + block.getCompartmentNumber() + 
//                                        ", Block " + block.getBlockName());
//        
//        return "redirect:/success";
//    }
    
    // NEW METHOD: View all blocks and compartments
//    @GetMapping("/view-blocks")
//    public String viewBlocks(Model model) {
//        List<Block> allBlocks = blockService.findAllBlocks();
//        
//        // Group blocks by compartment
//        Map<Integer, List<Block>> blocksByCompartment = allBlocks.stream()
//            .collect(Collectors.groupingBy(Block::getCompartmentNumber));
//        
//        // Get truck counts for each miller
//        Map<String, Integer> truckCounts = millerService.findAllMillers().stream()
//            .collect(Collectors.toMap(
//                Miller::getMillerCode,
//                miller -> truckService.countTrucksByMiller(miller)
//            ));
//        
//        model.addAttribute("blocksByCompartment", blocksByCompartment);
//        model.addAttribute("truckCounts", truckCounts);
//        
//        return "view-blocks";
//    }
    
    @GetMapping("/view-blocks")
    public String viewBlocks(Model model) {
    	
      List<Block> allBlocks = blockService.findAllBlocks();
      
      // Group blocks by compartment
      Map<Integer, List<Block>> blocksByCompartment = allBlocks.stream()
          .collect(Collectors.groupingBy(Block::getCompartmentNumber));
    	
//        Map<Integer, List<Block>> blocksByCompartment = blockService.getBlocksGroupedByCompartment();
//        Map<String, Integer> truckCounts = truckService.getTruckCountByMillerCode();
      Map<String, Integer> truckCounts = millerService.findAllMillers().stream()
            .collect(Collectors.toMap(
                Miller::getMillerCode,
                miller -> truckService.countTrucksByMiller(miller)
            ));
      

        int totalBlocks = blocksByCompartment.values().stream()
            .mapToInt(List::size).sum();

        int occupiedCount = blocksByCompartment.values().stream()
            .flatMap(List::stream)
            .mapToInt(b -> b.isOccupied() ? 1 : 0).sum();

        model.addAttribute("blocksByCompartment", blocksByCompartment);
        model.addAttribute("truckCounts", truckCounts);
        model.addAttribute("totalBlocks", totalBlocks);
        model.addAttribute("occupiedCount", occupiedCount);

        return "view-blocks";
    }

    
    // NEW METHOD: View detailed block information
    @GetMapping("/block-details/{blockId}")
    public String blockDetails(@PathVariable Long blockId, Model model) {
        Block block = blockService.findById(blockId);
        if (block == null) {
            return "redirect:/view-blocks";
        }
        
        Miller miller = block.getMiller();
        List<Truck> trucks = null;
        int truckCount = 0;
        
        if (miller != null) {
            trucks = truckService.findByMiller(miller);
            truckCount = trucks.size();
        }
        
        model.addAttribute("block", block);
        model.addAttribute("miller", miller);
        model.addAttribute("trucks", trucks);
        model.addAttribute("truckCount", truckCount);
        model.addAttribute("remainingCapacity", block.getCapacity() - truckCount);
        
        return "block-details";
    }
    
    @GetMapping("/success")
    public String success() {
        return "success";
    }
    
    @GetMapping("/search")
    public String searchForm() {
        return "search";
    }
    
    @GetMapping("/search-results")
    public String searchResults(@RequestParam(required = false) String millerCode,
                              @RequestParam(required = false) String truckName,
                              @RequestParam(required = false) LocalDate fromDate,
                              @RequestParam(required = false) LocalDate toDate,
                              Model model) {
        
        List<Truck> results = null;
        
        if (millerCode != null && !millerCode.isEmpty()) {
            Miller miller = millerService.findByMillerCode(millerCode);
            if (miller != null) {
                results = truckService.findByMiller(miller);
            }
        } else if (truckName != null && !truckName.isEmpty()) {
            results = truckService.findByTruckName(truckName);
        } else if (fromDate != null && toDate != null) {
            results = truckService.findByDateRange(fromDate, toDate);
        }
        
        model.addAttribute("results", results);
        return "search-results";
    }
    
    @PostMapping("/block-details/{blockId}/delete")
    public String deleteBlockRecords(@PathVariable Long blockId, RedirectAttributes redirectAttributes) {
        Block block = blockService.findById(blockId);
        if (block == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Block not found!");
            return "redirect:/view-blocks";
        }

        try {
            blockService.clearBlockData(blockId);
            redirectAttributes.addFlashAttribute("message", "Successfully cleared all records for Compartment " +
                    block.getCompartmentNumber() + ", Block " + block.getBlockName() + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error clearing block data: " + e.getMessage());
        }

        return "redirect:/view-blocks";
    }

    
}