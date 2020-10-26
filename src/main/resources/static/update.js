$('.status-btn').on('click', patchStatus);
$('.update-btn').on('click', showUpdateModal);
$('#update-submit').on('click', updateToDo);


function patchStatus() {
    var id = $(this).attr('data-id');
    var status = $(this).attr('data-status');
    if (status === 'open') {
        status = 'closed';
    } else {
        status = 'open';
    }
    var todo = {'id': id, 'status': status};
    $.ajax({
        url: '/todos/' + id,
        type: 'PATCH',
        contentType: 'application/json;utf-8',
        data: JSON.stringify(todo),
        success: function () {
            alert('처리되었습니다.');
            $('#delete-modal').modal('hide')
            location.reload();
        },
        error: function (xhr, status, error) {
            if (xhr.status >= 500) {
                alert('처리에 실패 했습니다.');
            } else {
                alert(xhr.responseJSON.message);
            }
            console.log(arguments);
        }
    });
}

function showUpdateModal() {
    var id = $(this).attr('data-id');
    var description = $(this).attr('data-description');
    var status = $(this).attr('data-status');
    var references = [];
    $('.update-' + id + '-references').each(function () {
        var reference = {}
        reference.id = $(this).data('reference-id');
        reference.referredId = $(this).data('reference-referred-id');
        references.push(reference);
    });
    var referenceDesc = '';
    if (references) {
        $.each(references, function (index, reference) {
            if (index > 0) {
                referenceDesc += ' ';
            }
            referenceDesc += ('@' + reference.referredId);
        })
    }
    $('#update-description').val(description);
    $('#update-references').val(referenceDesc);
    $('#update-references').attr('data-references', JSON.stringify(references));
    $('#update-id').val(id);
    $('#update-status').val(status);
    $('#update-modal').modal('show')
}

function updateToDo() {
    var id = $('#update-id').val();
    var description = $('#update-description').val();
    if (!description.trim()) {
        alert('내용을 입력해주세요');
        return;
    }
    var reference = $('#update-references').val();
    var references = []
    try {
        references = parse(reference);
    } catch (e) {
        alert(e);
        return;
    }

    var dataReferences = $('#update-references').attr('data-references');
    dataReferences = JSON.parse(dataReferences)
    for (i = 0; i < dataReferences.length; i++) {
        for (j = 0; j < references.length; j++) {
            if (references[j].referredId === dataReferences[i].referredId) {
                references[j].id = dataReferences[i].id;
            }
        }
    }
    var status = $('#update-status').val();
    var todo = {'id': id, 'description': description, 'references': references, 'status': status};

    $.ajax({
        url: '/todos/' + id,
        type: 'PUT',
        contentType: 'application/json;utf-8',
        data: JSON.stringify(todo),
        success: function () {
            alert('처리되었습니다.');
            $('#update-modal').modal('hide')
            location.reload();
        },
        error: function (xhr, status, error) {
            if (xhr.status >= 500) {
                alert('처리에 실패 했습니다.');
            } else {
                alert(xhr.responseJSON.message);
            }
            console.log(arguments);
        }
    });
}