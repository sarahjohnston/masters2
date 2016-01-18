namespace ApiReviews.Models
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;
    using System.Data.Entity.Spatial;

    [Table("sarahjdb.users")]
    public partial class user
    {
        public int Id { get; set; }

        [StringLength(200)]
        public string UserName { get; set; }
    }
}
