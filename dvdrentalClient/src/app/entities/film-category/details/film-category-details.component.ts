import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';

import { FilmCategoryService } from '../film-category.service';
import { IFilmCategory } from '../ifilm-category';
import { BaseDetailsComponent, Globals, PickerDialogService, ErrorService } from 'src/app/common/shared';
import { GlobalPermissionService } from 'src/app/core/global-permission.service';

import { CategoryService } from 'src/app/entities/category/category.service';
import { FilmService } from 'src/app/entities/film/film.service';

@Component({
  selector: 'app-film-category-details',
  templateUrl: './film-category-details.component.html',
  styleUrls: ['./film-category-details.component.scss'],
})
export class FilmCategoryDetailsComponent extends BaseDetailsComponent<IFilmCategory> implements OnInit {
  title = 'FilmCategory';
  parentUrl = 'filmcategory';
  constructor(
    public formBuilder: FormBuilder,
    public router: Router,
    public route: ActivatedRoute,
    public dialog: MatDialog,
    public global: Globals,
    public filmCategoryService: FilmCategoryService,
    public pickerDialogService: PickerDialogService,
    public errorService: ErrorService,
    public categoryService: CategoryService,
    public filmService: FilmService,
    public globalPermissionService: GlobalPermissionService
  ) {
    super(formBuilder, router, route, dialog, global, pickerDialogService, filmCategoryService, errorService);
  }

  ngOnInit() {
    this.entityName = 'FilmCategory';
    this.setAssociations();
    super.ngOnInit();
    this.setForm();
    this.getItem();
    this.setPickerSearchListener();
  }

  setForm() {
    this.itemForm = this.formBuilder.group({
      categoryId: ['', Validators.required],
      filmId: ['', Validators.required],
      categoryDescriptiveField: [''],
      filmDescriptiveField: [''],
    });

    this.fields = [];
  }

  onItemFetched(item: IFilmCategory) {
    this.item = item;
    this.itemForm.patchValue(item);
  }

  setAssociations() {
    this.associations = [
      {
        column: [
          {
            key: 'categoryId',
            value: undefined,
            referencedkey: 'categoryId',
          },
        ],
        isParent: false,
        table: 'category',
        type: 'ManyToOne',
        label: 'category',
        service: this.categoryService,
        descriptiveField: 'categoryDescriptiveField',
        referencedDescriptiveField: 'categoryId',
      },
      {
        column: [
          {
            key: 'filmId',
            value: undefined,
            referencedkey: 'filmId',
          },
        ],
        isParent: false,
        table: 'film',
        type: 'ManyToOne',
        label: 'film',
        service: this.filmService,
        descriptiveField: 'filmDescriptiveField',
        referencedDescriptiveField: 'filmId',
      },
    ];

    this.childAssociations = this.associations.filter((association) => {
      return association.isParent;
    });

    this.parentAssociations = this.associations.filter((association) => {
      return !association.isParent;
    });
  }

  onSubmit() {
    let filmCategory = this.itemForm.getRawValue();
    super.onSubmit(filmCategory);
  }
}