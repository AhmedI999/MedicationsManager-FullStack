import * as Yup from "yup";

export const createMedicationValidationSchema = () => {
    return Yup.object({
        pictureUrl: Yup.string()
            .trim()
            .url('Invalid URL')
            .matches(/\.(jpeg|jpg|gif|png)$/i, 'Invalid image URL')
            .required("Please leave the default picture if you don't want to add an image"),
        brandName: Yup.string()
            .trim()
            .required('Brand name is required'),
        timesDaily: Yup.number()
            .min(1, "Times must be greater than 0")
            .required('Times medicine taken daily is required'),
        instructions: Yup.string()
            .trim()
            .required('For safety reasons, instructions are required'),
    });
};
