import { FormControl } from "@angular/forms";

export interface LoginForm {
    username: FormControl<String>;
    password: FormControl<String>;
}