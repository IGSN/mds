package org.datacite.mds.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.util.Converters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RooWebScaffold(path = "datasets", formBackingObject = Dataset.class, delete = false)
@RequestMapping("/datasets")
@Controller
public class DatasetController {

    @Autowired
    private GenericConversionService myConversionService;

    @PostConstruct
    void registerConverters() {
        myConversionService.addConverter(Converters.getSimpleDatacentreConverter());
        myConversionService.addConverter(Converters.getSimpleDatasetConverter());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model model) {
        addDateTimeFormatPatterns(model);
        Dataset dataset = Dataset.findDataset(id);
        model.addAttribute("dataset", dataset);
        model.addAttribute("metadatas", Metadata.findMetadatasByDataset(dataset).getResultList());
        model.addAttribute("itemId", id);
        return "datasets/show";
    }

    @ModelAttribute("datacentres")
    public Collection<Datacentre> populateDatacentres(HttpServletRequest request) {
        String symbol = request.getUserPrincipal().getName();
        Datacentre datacentre = Datacentre.findDatacentresBySymbolEquals(symbol).getSingleResult();
        return Arrays.asList(datacentre);
    }
}
