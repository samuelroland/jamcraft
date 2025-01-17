import { useState } from 'react';

import { Sample } from '../../types';
import { toast } from 'react-toastify';

type Props = {
    onSuccessfulUpload: (sample: Sample) => void;
};

const UploadDropZone = ({ onSuccessfulUpload }: Props) => {
    const [hoveringDropZone, setHoveringDropZone] = useState(false);

    const handleDrop = async (event: React.DragEvent<HTMLDivElement>) => {
        event.preventDefault();
        setHoveringDropZone(false);
        let name = '';
        while ((name = (prompt('Give a name to this sample') ?? '').trim()).length == 0) {}

        const files = event.dataTransfer.files;
        if (files.length) {
            const file = files[0];
            // Handle the file upload here
            const formData = new FormData();
            formData.append('name', name);
            formData.append('file', file);

            try {
                const response = await fetch('/samples', {
                    method: 'POST',
                    body: formData,
                });

                if (response.ok) {
                    const sample = JSON.parse(await response.text()) as Sample;
                    onSuccessfulUpload(sample);
                    toast.success("Sample '" + sample.name + "' uploaded successfully");
                } else {
                    const error = JSON.parse(await response.text()) as { message: string };
                    toast.error(error.message);
                    console.error(error.message);
                }
            } catch (error) {
                console.error('Error uploading file:', error);
            }
        }
    };

    return (
        <div
            className={'border border-gray-600 p-1 relative rounded-sm' + (hoveringDropZone ? ' bg-orange-200' : '')}
            onMouseOver={() => setHoveringDropZone(true)}
            onMouseOut={() => setHoveringDropZone(false)}
            onDragOver={(event) => {
                event.preventDefault(); // do this or the browser will open the file...
                setHoveringDropZone(true);
            }}
            onDragLeave={(e) => {
                e.preventDefault();
                setHoveringDropZone(false);
            }}
            onDrop={handleDrop}
        >
            <p className={'my-2 text-gray-700' + (hoveringDropZone ? ' font-bold' : '')}>Drop MP3 file here to upload</p>
        </div>
    );
};

export default UploadDropZone;
